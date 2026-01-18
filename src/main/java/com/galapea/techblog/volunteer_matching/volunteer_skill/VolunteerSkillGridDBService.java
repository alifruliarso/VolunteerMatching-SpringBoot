package com.galapea.techblog.volunteer_matching.volunteer_skill;

import com.galapea.techblog.volunteer_matching.griddb.GridDbClient;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsRequest;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsResponse;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbCloudSQLStmt;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbColumn;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbContainerDefinition;
import com.galapea.techblog.volunteer_matching.griddbwebapi.SQLSelectResponse;
import com.galapea.techblog.volunteer_matching.util.DateTimeUtil;
import com.galapea.techblog.volunteer_matching.util.NotFoundException;
import com.github.f4b6a3.tsid.TsidCreator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VolunteerSkillGridDBService implements VolunteerSkillService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbClient gridDbClient;
    private final String TBL_NAME = "VoMaVolunteerSkills";

    public VolunteerSkillGridDBService(final GridDbClient gridDbClient) {
        this.gridDbClient = gridDbClient;
    }

    public void createTable() {
        List<GridDbColumn> columns = List.of(
                new GridDbColumn("id", "STRING", Set.of("TREE")),
                new GridDbColumn("userId", "STRING", Set.of("TREE")),
                new GridDbColumn("skillId", "STRING", Set.of("TREE")),
                new GridDbColumn("expiryDate", "TIMESTAMP"),
                new GridDbColumn("verificationStatus", "STRING"));

        GridDbContainerDefinition containerDefinition = GridDbContainerDefinition.build(TBL_NAME, columns);
        this.gridDbClient.createContainer(containerDefinition);
    }

    @Override
    public List<VolunteerSkillDTO> findAll() {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(50L).sort("id ASC").build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        return response.getRows().stream()
                .map(row -> {
                    return extractDTO(row);
                })
                .collect(Collectors.toList());
    }

    private VolunteerSkillDTO extractDTO(List<Object> row) {
        log.info("############################### {}", row);
        VolunteerSkillDTO dto = new VolunteerSkillDTO();
        dto.setId((String) row.get(0));
        dto.setUserId((String) row.get(1));
        dto.setSkillId((String) row.get(2));
        Object expiry = row.get(3);
        if (expiry != null) {
            try {
                dto.setExpiryDate(DateTimeUtil.parseToLocalDateTime(expiry.toString()));
            } catch (Exception e) {
                dto.setExpiryDate(null);
            }
        }
        Object vs = row.get(4);
        if (vs != null) {
            try {
                dto.setVerificationStatus(VerificationStatus.valueOf(vs.toString()));
            } catch (Exception e) {
                dto.setVerificationStatus(null);
            }
        }
        try {
            dto.setSkillName((String) row.get(5));
        } catch (Exception e) {
            dto.setSkillName(null);
        }
        return dto;
    }

    @Override
    public VolunteerSkillDTO get(final String id) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("id == '" + id + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            throw new NotFoundException("VolunteerSkill not found with id: " + id);
        }
        return response.getRows().stream()
                .findFirst()
                .map(row -> {
                    return extractDTO(row);
                })
                .orElseThrow(() -> new NotFoundException("VolunteerSkill not found with id: " + id));
    }

    public String nextId() {
        return TsidCreator.getTsid().format("vsk_%s");
    }

    @Override
    public String create(final VolunterSkillAddRequest volunteerSkillDTO) {
        String id =
                volunteerSkillDTO.getId() != null && !volunteerSkillDTO.getId().isBlank()
                        ? volunteerSkillDTO.getId()
                        : nextId();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(volunteerSkillDTO.getUserId())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(volunteerSkillDTO.getSkillId())).append("\"");
        sb.append(", ");
        sb.append(
                volunteerSkillDTO.getExpiryDate() == null
                        ? "null"
                        : "\"" + DateTimeUtil.formatToZoneDateTimeString(volunteerSkillDTO.getExpiryDate()) + "\"");
        sb.append(", ");
        sb.append(
                volunteerSkillDTO.getVerificationStatus() == null
                        ? "null"
                        : "\""
                                + escapeString(volunteerSkillDTO
                                        .getVerificationStatus()
                                        .name()) + "\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Create VolunteerSkill: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
        return id;
    }

    @Override
    public void update(final String id, final VolunteerSkillDTO volunteerSkillDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(volunteerSkillDTO.getUserId())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(volunteerSkillDTO.getSkillId())).append("\"");
        sb.append(", ");
        sb.append(
                volunteerSkillDTO.getExpiryDate() == null
                        ? "null"
                        : "\"" + DateTimeUtil.formatToZoneDateTimeString(volunteerSkillDTO.getExpiryDate()) + "\"");
        sb.append(", ");
        sb.append(
                volunteerSkillDTO.getVerificationStatus() == null
                        ? "null"
                        : "\""
                                + escapeString(volunteerSkillDTO
                                        .getVerificationStatus()
                                        .name()) + "\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Update VolunteerSkill: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
    }

    @Override
    public void delete(final String id) {
        throw new UnsupportedOperationException("GridDB VolunteerSkill service not implemented");
    }

    @Override
    public boolean idExists(final String id) {
        VolunteerSkillDTO vs = get(id);
        return vs != null;
    }

    private String escapeString(String input) {
        if (input == null) return null;
        return input.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"");
    }

    @Override
    public List<VolunteerSkillDTO> findAllByUserId(String userId) {
        // @formatter:off
        String stmt =  """
            SELECT vs.id, vs.userId, vs.skillId, vs.expiryDate, vs.verificationStatus, sk.name \
            FROM VoMaVolunteerSkills vs \
            LEFT JOIN VoMaSkills sk ON vs.skillId = sk.id \
            WHERE vs.userId = '%s'
        """.formatted(userId);
        // @formatter:on
        List<GridDbCloudSQLStmt> statementList = List.of(new GridDbCloudSQLStmt(stmt));
        SQLSelectResponse[] response = this.gridDbClient.select(statementList);
        if (response == null || response.length != statementList.size()) {
            log.error(
                    "Failed to fetch VolunteerSkills by user {}. Response is null or size mismatch. Expected: {}, Actual: {}",
                    userId,
                    statementList.size(),
                    response != null ? response.length : 0);
            return List.of();
        }

        List<List<Object>> results = response[0].getResults();
        if (results.isEmpty()) {
            log.info("No result for VolunteerSkills userId: {}", userId);
            return List.of();
        }
        return results.stream()
                .map(row -> {
                    return extractDTO(row);
                })
                .collect(Collectors.toList());
    }
}
