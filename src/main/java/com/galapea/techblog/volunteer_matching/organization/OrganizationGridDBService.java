package com.galapea.techblog.volunteer_matching.organization;

import com.galapea.techblog.volunteer_matching.griddb.GridDbClient;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsRequest;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsResponse;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbColumn;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbContainerDefinition;
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
public class OrganizationGridDBService implements OrganizationService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbClient gridDbClient;
    private final String TBL_NAME = "VoMaOrganizations";

    public OrganizationGridDBService(final GridDbClient gridDbClient) {
        this.gridDbClient = gridDbClient;
    }

    public void createTable() {
        List<GridDbColumn> columns = List.of(
                new GridDbColumn("id", "STRING", Set.of("TREE")),
                new GridDbColumn("name", "STRING", Set.of("TREE")),
                new GridDbColumn("websiteUrl", "STRING"),
                new GridDbColumn("adminUserId", "STRING", Set.of("TREE")));

        GridDbContainerDefinition containerDefinition = GridDbContainerDefinition.build(TBL_NAME, columns);
        this.gridDbClient.createContainer(containerDefinition);
    }

    @Override
    public List<OrganizationDTO> findAll() {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(50L).sort("id ASC").build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        return response.getRows().stream()
                .map(row -> {
                    OrganizationDTO dto = new OrganizationDTO();
                    dto.setId((String) row.get(0));
                    dto.setName((String) row.get(1));
                    dto.setWebsiteUrl((String) row.get(2));
                    dto.setAdminUserId((String) row.get(3));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public OrganizationDTO get(final String id) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("id == '" + id + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            throw new NotFoundException("Organization not found with id: " + id);
        }
        return response.getRows().stream()
                .findFirst()
                .map(row -> {
                    OrganizationDTO dto = new OrganizationDTO();
                    dto.setId((String) row.get(0));
                    dto.setName((String) row.get(1));
                    dto.setWebsiteUrl((String) row.get(2));
                    dto.setAdminUserId((String) row.get(3));
                    return dto;
                })
                .orElseThrow(() -> new NotFoundException("Organization not found with id: " + id));
    }

    public String nextId() {
        return TsidCreator.getTsid().format("org_%s");
    }

    @Override
    public String create(final OrganizationDTO organizationDTO) {
        String id = organizationDTO.getId() != null && !organizationDTO.getId().isBlank()
                ? organizationDTO.getId()
                : nextId();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(organizationDTO.getName())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(organizationDTO.getWebsiteUrl())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(organizationDTO.getAdminUserId())).append("\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Create Organization: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
        return id;
    }

    @Override
    public void update(final String id, final OrganizationDTO organizationDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(organizationDTO.getName())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(organizationDTO.getWebsiteUrl())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(organizationDTO.getAdminUserId())).append("\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Update Organization: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
    }

    @Override
    public void delete(final String id) {
        throw new UnsupportedOperationException("GridDB Organization service not implemented");
    }

    @Override
    public boolean idExists(final String id) {
        OrganizationDTO org = get(id);
        return org != null;
    }

    public Optional<OrganizationDTO> getOneByName(final String name) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("name == '" + name + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        OrganizationDTO dto = response.getRows().stream()
                .findFirst()
                .map(row -> {
                    OrganizationDTO d = new OrganizationDTO();
                    d.setId((String) row.get(0));
                    d.setName((String) row.get(1));
                    d.setWebsiteUrl((String) row.get(2));
                    d.setAdminUserId((String) row.get(3));
                    return d;
                })
                .orElse(null);
        return Optional.ofNullable(dto);
    }

    public Optional<OrganizationDTO> getOneByAdminUserId(final String adminUserId) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("adminUserId == '" + adminUserId + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        OrganizationDTO dto = response.getRows().stream()
                .findFirst()
                .map(row -> {
                    OrganizationDTO d = new OrganizationDTO();
                    d.setId((String) row.get(0));
                    d.setName((String) row.get(1));
                    d.setWebsiteUrl((String) row.get(2));
                    d.setAdminUserId((String) row.get(3));
                    return d;
                })
                .orElse(null);
        return Optional.ofNullable(dto);
    }

    private String escapeString(String input) {
        if (input == null) return null;
        return input.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"");
    }
}
