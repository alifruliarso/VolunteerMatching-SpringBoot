package com.galapea.techblog.volunteer_matching.opportunity;

import com.galapea.techblog.volunteer_matching.griddb.GridDbClient;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsRequest;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsResponse;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbColumn;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbContainerDefinition;
import com.galapea.techblog.volunteer_matching.util.DateTimeUtil;
import com.galapea.techblog.volunteer_matching.util.NotFoundException;
import com.github.f4b6a3.tsid.TsidCreator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OpportunityGridDBService implements OpportunityService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbClient gridDbClient;
    private final String TBL_NAME = "VoMaOpportunities";

    public OpportunityGridDBService(final GridDbClient gridDbClient) {
        this.gridDbClient = gridDbClient;
    }

    public void createTable() {
        List<GridDbColumn> columns = List.of(
                new GridDbColumn("id", "STRING", Set.of("TREE")),
                new GridDbColumn("title", "STRING", Set.of("TREE")),
                new GridDbColumn("description", "STRING"),
                new GridDbColumn("address", "STRING"),
                new GridDbColumn("startTime", "TIMESTAMP"),
                new GridDbColumn("endTime", "TIMESTAMP"),
                new GridDbColumn("slotsTotal", "LONG"),
                new GridDbColumn("orgId", "STRING", Set.of("TREE")));

        GridDbContainerDefinition containerDefinition = GridDbContainerDefinition.build(TBL_NAME, columns);
        this.gridDbClient.createContainer(containerDefinition);
    }

    @Override
    public List<OpportunityDTO> findAll() {
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

    private OpportunityDTO mapRowToDTO(List<Object> row) {
        OpportunityDTO dto = new OpportunityDTO();
        dto.setId((String) row.get(0));
        dto.setTitle((String) row.get(1));
        dto.setDescription((String) row.get(2));
        dto.setAddress((String) row.get(3));
        Object st = row.get(4);
        if (st != null) {
            try {
                dto.setStartTime(DateTimeUtil.parseToLocalDateTime(st.toString()));
            } catch (Exception e) {
                dto.setStartTime(null);
            }
        }
        Object et = row.get(5);
        if (et != null) {
            try {
                dto.setEndTime(DateTimeUtil.parseToLocalDateTime(et.toString()));
            } catch (Exception e) {
                dto.setEndTime(null);
            }
        }
        Object slots = row.get(6);
        if (slots != null) {
            try {
                if (slots instanceof Number) dto.setSlotsTotal(((Number) slots).longValue());
                else dto.setSlotsTotal(Long.valueOf(slots.toString()));
            } catch (Exception e) {
                dto.setSlotsTotal(null);
            }
        }
        dto.setOrgId((String) row.get(7));
        return dto;
    }

    @Override
    public OpportunityDTO get(final String id) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("id == '" + id + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            throw new NotFoundException("Opportunity not found with id: " + id);
        }
        return response.getRows().stream()
                .findFirst()
                .map(row -> {
                    return mapRowToDTO(row);
                })
                .orElseThrow(() -> new NotFoundException("Opportunity not found with id: " + id));
    }

    public String nextId() {
        return TsidCreator.getTsid().format("opp_%s");
    }

    @Override
    public String create(final OpportunityDTO opportunityDTO) {
        String id =
                opportunityDTO.getId() != null && !opportunityDTO.getId().isBlank() ? opportunityDTO.getId() : nextId();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(opportunityDTO.getTitle())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(opportunityDTO.getDescription())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(opportunityDTO.getAddress())).append("\"");
        sb.append(", ");
        sb.append(
                opportunityDTO.getStartTime() == null
                        ? "null"
                        : "\"" + DateTimeUtil.formatToZoneDateTimeString(opportunityDTO.getStartTime()) + "\"");
        sb.append(", ");
        sb.append(
                opportunityDTO.getEndTime() == null
                        ? "null"
                        : "\"" + DateTimeUtil.formatToZoneDateTimeString(opportunityDTO.getEndTime()) + "\"");
        sb.append(", ");
        sb.append(
                opportunityDTO.getSlotsTotal() == null
                        ? "null"
                        : opportunityDTO.getSlotsTotal().toString());
        sb.append(", ");
        sb.append("\"").append(escapeString(opportunityDTO.getOrgId())).append("\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Create Opportunity: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
        return id;
    }

    @Override
    public void update(final String id, final OpportunityDTO opportunityDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(opportunityDTO.getTitle())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(opportunityDTO.getDescription())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(opportunityDTO.getAddress())).append("\"");
        sb.append(", ");
        sb.append(
                opportunityDTO.getStartTime() == null
                        ? "null"
                        : "\"" + DateTimeUtil.formatToZoneDateTimeString(opportunityDTO.getStartTime()) + "\"");
        sb.append(", ");
        sb.append(
                opportunityDTO.getEndTime() == null
                        ? "null"
                        : "\"" + DateTimeUtil.formatToZoneDateTimeString(opportunityDTO.getEndTime()) + "\"");
        sb.append(", ");
        sb.append(
                opportunityDTO.getSlotsTotal() == null
                        ? "null"
                        : opportunityDTO.getSlotsTotal().toString());
        sb.append(", ");
        sb.append("\"").append(escapeString(opportunityDTO.getOrgId())).append("\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Update Opportunity: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
    }

    @Override
    public void delete(final String id) {
        throw new UnsupportedOperationException("GridDB Opportunity service not implemented");
    }

    @Override
    public boolean idExists(final String id) {
        OpportunityDTO o = get(id);
        return o != null;
    }

    private String escapeString(String input) {
        if (input == null) return null;
        return input.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"");
    }

    @Override
    public Optional<OpportunityDTO> getOneById(String id) {
        try {
            OpportunityDTO opportunity = get(id);
            return Optional.ofNullable(opportunity);
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<OpportunityDTO> findAllByOrgId(String orgId) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(50L)
                .condition("orgId == '" + orgId + "'")
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
}
