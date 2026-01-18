package com.galapea.techblog.volunteer_matching.registration;

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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegistrationGridDBService implements RegistrationService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbClient gridDbClient;
    private final String TBL_NAME = "VoMaRegistrations";

    public RegistrationGridDBService(final GridDbClient gridDbClient) {
        this.gridDbClient = gridDbClient;
    }

    public void createTable() {
        List<GridDbColumn> columns = List.of(
                new GridDbColumn("id", "STRING", Set.of("TREE")),
                new GridDbColumn("userId", "STRING", Set.of("TREE")),
                new GridDbColumn("opportunityId", "STRING", Set.of("TREE")),
                new GridDbColumn("status", "STRING"),
                new GridDbColumn("registrationTime", "TIMESTAMP"));

        GridDbContainerDefinition containerDefinition = GridDbContainerDefinition.build(TBL_NAME, columns);
        this.gridDbClient.createContainer(containerDefinition);
    }

    @Override
    public List<RegistrationDTO> findAll() {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(50L).sort("id ASC").build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        return response.getRows().stream()
                .map(row -> {
                    return extractRowToDTO(row);
                })
                .collect(Collectors.toList());
    }

    private RegistrationDTO extractRowToDTO(List<Object> row) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId((String) row.get(0));
        dto.setUserId((String) row.get(1));
        dto.setOpportunityId((String) row.get(2));
        try {
            dto.setStatus(RegistrationStatus.valueOf(row.get(3).toString()));
        } catch (Exception e) {
            dto.setStatus(null);
        }
        try {
            dto.setRegistrationTime(DateTimeUtil.parseToLocalDateTime(row.get(4).toString()));
        } catch (Exception e) {
            dto.setRegistrationTime(null);
        }
        return dto;
    }

    @Override
    public RegistrationDTO get(final String id) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("id == '" + id + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            throw new NotFoundException("Registration not found with id: " + id);
        }
        return response.getRows().stream()
                .findFirst()
                .map(row -> {
                    return extractRowToDTO(row);
                })
                .orElseThrow(() -> new NotFoundException("Registration not found with id: " + id));
    }

    public String nextId() {
        return TsidCreator.getTsid().format("reg_%s");
    }

    @Override
    public String create(final RegistrationDTO registrationDTO) {
        String id = registrationDTO.getId() != null && !registrationDTO.getId().isBlank()
                ? registrationDTO.getId()
                : nextId();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(registrationDTO.getUserId())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(registrationDTO.getOpportunityId())).append("\"");
        sb.append(", ");
        sb.append(
                registrationDTO.getStatus() == null
                        ? "null"
                        : "\"" + escapeString(registrationDTO.getStatus().name()) + "\"");
        sb.append(", ");
        sb.append("\"")
                .append(DateTimeUtil.formatToZoneDateTimeString(registrationDTO.getRegistrationTime()))
                .append("\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Create Registration: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
        return id;
    }

    @Override
    public void update(final String id, final RegistrationDTO registrationDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(registrationDTO.getUserId())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(registrationDTO.getOpportunityId())).append("\"");
        sb.append(", ");
        sb.append(
                registrationDTO.getStatus() == null
                        ? "null"
                        : "\"" + escapeString(registrationDTO.getStatus().name()) + "\"");
        sb.append(", ");
        sb.append(
                registrationDTO.getRegistrationTime() == null
                        ? "null"
                        : "\""
                                + escapeString(
                                        registrationDTO.getRegistrationTime().toString()) + "\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Update Registration: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
    }

    @Override
    public void delete(final String id) {
        throw new UnsupportedOperationException("GridDB Registration service not implemented");
    }

    @Override
    public boolean idExists(final String id) {
        RegistrationDTO r = get(id);
        return r != null;
    }

    private String escapeString(String input) {
        if (input == null) return null;
        return input.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"");
    }

    @Override
    public String register(String userId, String opportunityId) {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUserId(userId);
        registrationDTO.setOpportunityId(opportunityId);
        registrationDTO.setStatus(RegistrationStatus.PENDING);
        registrationDTO.setRegistrationTime(LocalDateTime.now());
        return create(registrationDTO);
    }

    @Override
    public Optional<RegistrationDTO> getByUserIdAndOpportunityId(String userId, String opportunityId) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("userId == '" + userId + "' and opportunityId == '" + opportunityId + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB. userId: " + userId + ", opportunityId: " + opportunityId);
            return Optional.empty();
        }
        RegistrationDTO result = response.getRows().stream()
                .findFirst()
                .map(row -> {
                    return extractRowToDTO(row);
                })
                .orElse(null);
        return Optional.ofNullable(result);
    }

    @Override
    public Long countByOpportunityId(String opportunityId) {
        // @formatter:off
        String stmt =  """
            SELECT count(id) FROM VoMaRegistrations \
            WHERE opportunityId = '%s'
        """.formatted(opportunityId);
        // @formatter:on
        List<GridDbCloudSQLStmt> statementList = List.of(new GridDbCloudSQLStmt(stmt));
        SQLSelectResponse[] response = this.gridDbClient.select(statementList);
        if (response == null || response.length != statementList.size()) {
            log.error(
                    "Failed to count registered {}. Response is null or size mismatch. Expected: {}, Actual: {}",
                    opportunityId,
                    statementList.size(),
                    response != null ? response.length : 0);
            return 0L;
        }

        List<List<Object>> results = response[0].getResults();
        if (results.isEmpty()) {
            log.info("No result for counting opportunity: {}", opportunityId);
            return 0L;
        }

        Long count = Long.parseLong(results.get(0).get(0).toString());
        log.info("Counted {} registered for opportunity: {}", count, opportunityId);
        return count;
    }

    @Override
    public void approve(String id) {
        RegistrationDTO registrationDTO = get(id);
        if (registrationDTO.getStatus().compareTo(RegistrationStatus.PENDING) != 0) {
            throw new IllegalStateException("Only PENDING registrations can be approved");
        }
        registrationDTO.setStatus(RegistrationStatus.APPROVED);
        update(id, registrationDTO);
    }

    @Override
    public void reject(String id) {
        RegistrationDTO registrationDTO = get(id);
        if (registrationDTO.getStatus().compareTo(RegistrationStatus.PENDING) != 0) {
            throw new IllegalStateException("Only PENDING registrations can be cancelled");
        }
        registrationDTO.setStatus(RegistrationStatus.CANCELLED);
        update(id, registrationDTO);
    }

    @Override
    public List<RegistrationDTO> findAllByOrgId(String orgId) {
        // @formatter:off
        String stmt =  """
            SELECT reg.id, reg.userId, reg.opportunityId, reg.status, reg.registrationTime \
            FROM VoMaRegistrations reg \
            JOIN VoMaOpportunities opp ON reg.opportunityId = opp.id \
            WHERE opp.orgId = '%s'
        """.formatted(orgId);
        // @formatter:on
        List<GridDbCloudSQLStmt> statementList = List.of(new GridDbCloudSQLStmt(stmt));
        SQLSelectResponse[] response = this.gridDbClient.select(statementList);
        if (response == null || response.length != statementList.size()) {
            log.error(
                    "Failed to fetch registered by org {}. Response is null or size mismatch. Expected: {}, Actual: {}",
                    orgId,
                    statementList.size(),
                    response != null ? response.length : 0);
            return List.of();
        }

        List<List<Object>> results = response[0].getResults();
        if (results.isEmpty()) {
            log.info("No result for opportunity: {}", orgId);
            return List.of();
        }
        return results.stream()
                .map(row -> {
                    return extractRowToDTO(row);
                })
                .collect(Collectors.toList());
    }
}
