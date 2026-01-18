package com.galapea.techblog.volunteer_matching.opportunity_requirement;

import com.galapea.techblog.volunteer_matching.griddb.GridDbClient;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsRequest;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsResponse;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbCloudSQLStmt;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbColumn;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbContainerDefinition;
import com.galapea.techblog.volunteer_matching.griddbwebapi.SQLUpdateResponse;
import com.galapea.techblog.volunteer_matching.util.NotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OpportunityRequirementGridDBService implements OpportunityRequirementService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbClient gridDbClient;
    private final String TBL_NAME = "VoMaOpportunityRequirements";

    public OpportunityRequirementGridDBService(final GridDbClient gridDbClient) {
        this.gridDbClient = gridDbClient;
    }

    public void createTable() {
        List<GridDbColumn> columns = List.of(
                new GridDbColumn("id", "STRING"),
                new GridDbColumn("opportunityId", "STRING", Set.of("TREE")),
                new GridDbColumn("skillId", "STRING", Set.of("TREE")),
                new GridDbColumn("isMandatory", "BOOL"));

        GridDbContainerDefinition containerDefinition = GridDbContainerDefinition.build(TBL_NAME, columns);
        this.gridDbClient.createContainer(containerDefinition);
    }

    @Override
    public List<OpportunityRequirementDTO> findAll() {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(50L).sort("id ASC").build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        return response.getRows().stream()
                .map(row -> {
                    return mapRowToDTO(row);
                })
                .collect(Collectors.toList());
    }

    @Override
    public OpportunityRequirementDTO get(final String id) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("id == '" + id + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            throw new NotFoundException("OpportunityRequirement not found with id: " + id);
        }
        return response.getRows().stream()
                .findFirst()
                .map(row -> {
                    return mapRowToDTO(row);
                })
                .orElseThrow(() -> new NotFoundException("OpportunityRequirement not found with id: " + id));
    }

    private OpportunityRequirementDTO mapRowToDTO(List<Object> row) {
        OpportunityRequirementDTO dto = new OpportunityRequirementDTO();
        Object rid = row.get(0);
        if (rid != null) {
            try {
                dto.setId(rid.toString());
            } catch (Exception e) {
                dto.setId(null);
            }
        }
        dto.setOpportunityId((String) row.get(1));
        dto.setSkillId((String) row.get(2));
        Object im = row.get(3);
        if (im != null) {
            try {
                if (im instanceof Boolean) dto.setIsMandatory((Boolean) im);
                else dto.setIsMandatory(Boolean.valueOf(im.toString()));
            } catch (Exception e) {
                dto.setIsMandatory(null);
            }
        }
        return dto;
    }

    @Override
    public String create(final OpportunityRequirementDTO opportunityRequirementDTO) {
        String opportunityId = escapeString(opportunityRequirementDTO.getOpportunityId());
        String skillId = escapeString(opportunityRequirementDTO.getSkillId());
        String id = opportunityRequirementDTO.getId() != null
                ? opportunityRequirementDTO.getId()
                : opportunityId + "-" + skillId;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(opportunityId).append("\"");
        sb.append(", ");
        sb.append("\"").append(skillId).append("\"");
        sb.append(", ");
        sb.append(
                opportunityRequirementDTO.getIsMandatory() == null
                        ? "null"
                        : escapeString(
                                opportunityRequirementDTO.getIsMandatory().toString()));
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Create OpportunityRequirement: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
        return id;
    }

    @Override
    public void update(final String id, final OpportunityRequirementDTO opportunityRequirementDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append(id.toString());
        sb.append(", ");
        sb.append("\"")
                .append(escapeString(opportunityRequirementDTO.getOpportunityId()))
                .append("\"");
        sb.append(", ");
        sb.append("\"")
                .append(escapeString(opportunityRequirementDTO.getSkillId()))
                .append("\"");
        sb.append(", ");
        sb.append("\'")
                .append(
                        opportunityRequirementDTO.getIsMandatory() == null
                                ? "null"
                                : escapeString(opportunityRequirementDTO
                                        .getIsMandatory()
                                        .toString()));
        sb.append("\'");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Update OpportunityRequirement: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
    }

    @Override
    public void delete(final String id) {
        throw new UnsupportedOperationException("GridDB OpportunityRequirement service not implemented");
    }

    private String escapeString(String input) {
        if (input == null) return null;
        return input.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"");
    }

    @Override
    public List<OpportunityRequirementDTO> findAllByOpportunityId(String opportunityId) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(50L)
                .condition("opportunityId == '" + opportunityId + "'")
                .sort("id ASC")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        return response.getRows().stream()
                .map(row -> {
                    return mapRowToDTO(row);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void replaceAllForOpportunity(String opportunityId, List<OpportunityRequirementDTO> requirements) {
        String statementDelete = "DELETE FROM " + TBL_NAME + " WHERE opportunityId = '" + opportunityId + "'";
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO " + TBL_NAME + " (id, opportunityId, skillId, isMandatory) VALUES ");
        for (int i = 0; i < requirements.size(); i++) {
            OpportunityRequirementDTO req = requirements.get(i);
            String reqOpportunityId = req.getOpportunityId();
            String reqSkillId = req.getSkillId();
            String reqId = reqOpportunityId + "-" + reqSkillId;
            sb.append("(");
            sb.append("\'").append(reqId).append("\'");
            sb.append(", ");
            sb.append("\'").append(reqOpportunityId).append("\'");
            sb.append(", ");
            sb.append("\'").append(reqSkillId).append("\'");
            sb.append(", ");
            sb.append(
                    req.getIsMandatory() == null
                            ? false
                            : escapeString(req.getIsMandatory().toString()));
            sb.append(")");
            if (i < requirements.size() - 1) {
                sb.append(", ");
            }
        }
        List<GridDbCloudSQLStmt> sqlStmts =
                List.of(new GridDbCloudSQLStmt(statementDelete), new GridDbCloudSQLStmt(sb.toString()));
        SQLUpdateResponse[] responses = this.gridDbClient.executeSQLUpdate(sqlStmts);
        log.info("!!!!!!!!!!!!!!!!Replaced all OpportunityRequirements for opportunityId {}: ", opportunityId);
        for (SQLUpdateResponse sqlUpdateResponse : responses) {
            log.info("{}", sqlUpdateResponse);
        }
    }
}
