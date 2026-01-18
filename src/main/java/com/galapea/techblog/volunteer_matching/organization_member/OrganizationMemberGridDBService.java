package com.galapea.techblog.volunteer_matching.organization_member;

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
public class OrganizationMemberGridDBService implements OrganizationMemberService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbClient gridDbClient;
    private final String TBL_NAME = "VoMaOrganizationMembers";

    public OrganizationMemberGridDBService(final GridDbClient gridDbClient) {
        this.gridDbClient = gridDbClient;
    }

    public void createTable() {
        List<GridDbColumn> columns = List.of(
                new GridDbColumn("id", "STRING", Set.of("TREE")),
                new GridDbColumn("userId", "STRING", Set.of("TREE")),
                new GridDbColumn("orgId", "STRING", Set.of("TREE")),
                new GridDbColumn("memberRole", "STRING"));

        GridDbContainerDefinition containerDefinition = GridDbContainerDefinition.build(TBL_NAME, columns);
        this.gridDbClient.createContainer(containerDefinition);
    }

    @Override
    public List<OrganizationMemberDTO> findAll() {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(50L).sort("id ASC").build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        return response.getRows().stream()
                .map(row -> {
                    OrganizationMemberDTO dto = new OrganizationMemberDTO();
                    dto.setId((String) row.get(0));
                    dto.setUserId((String) row.get(1));
                    dto.setOrgId((String) row.get(2));
                    Object mr = row.get(3);
                    if (mr != null) {
                        try {
                            dto.setMemberRole(MemberRole.valueOf(mr.toString()));
                        } catch (Exception e) {
                            dto.setMemberRole(null);
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public OrganizationMemberDTO get(final String id) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("id == '" + id + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            throw new NotFoundException("OrganizationMember not found with id: " + id);
        }
        return response.getRows().stream()
                .findFirst()
                .map(row -> {
                    OrganizationMemberDTO dto = new OrganizationMemberDTO();
                    dto.setId((String) row.get(0));
                    dto.setUserId((String) row.get(1));
                    dto.setOrgId((String) row.get(2));
                    Object mr = row.get(3);
                    if (mr != null) {
                        try {
                            dto.setMemberRole(MemberRole.valueOf(mr.toString()));
                        } catch (Exception e) {
                            dto.setMemberRole(null);
                        }
                    }
                    return dto;
                })
                .orElseThrow(() -> new NotFoundException("OrganizationMember not found with id: " + id));
    }

    public String nextId() {
        return TsidCreator.getTsid().format("om_%s");
    }

    @Override
    public String create(final OrganizationMemberDTO organizationMemberDTO) {
        String id = organizationMemberDTO.getId() != null
                        && !organizationMemberDTO.getId().isBlank()
                ? organizationMemberDTO.getId()
                : nextId();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(organizationMemberDTO.getUserId())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(organizationMemberDTO.getOrgId())).append("\"");
        sb.append(", ");
        sb.append(
                organizationMemberDTO.getMemberRole() == null
                        ? "null"
                        : "\""
                                + escapeString(
                                        organizationMemberDTO.getMemberRole().name()) + "\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Create OrganizationMember: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
        return id;
    }

    @Override
    public void update(final String id, final OrganizationMemberDTO organizationMemberDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(organizationMemberDTO.getUserId())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(organizationMemberDTO.getOrgId())).append("\"");
        sb.append(", ");
        sb.append(
                organizationMemberDTO.getMemberRole() == null
                        ? "null"
                        : "\""
                                + escapeString(
                                        organizationMemberDTO.getMemberRole().name()) + "\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Update OrganizationMember: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
    }

    @Override
    public void delete(final String id) {
        throw new UnsupportedOperationException("GridDB OrganizationMember service not implemented");
    }

    @Override
    public boolean idExists(final String id) {
        OrganizationMemberDTO om = get(id);
        return om != null;
    }

    public Optional<OrganizationMemberDTO> getOneByUserId(final String userId) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("userId == '" + userId + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        OrganizationMemberDTO dto = response.getRows().stream()
                .findFirst()
                .map(row -> {
                    OrganizationMemberDTO d = new OrganizationMemberDTO();
                    d.setId((String) row.get(0));
                    d.setUserId((String) row.get(1));
                    d.setOrgId((String) row.get(2));
                    Object mr = row.get(3);
                    if (mr != null) {
                        try {
                            d.setMemberRole(MemberRole.valueOf(mr.toString()));
                        } catch (Exception e) {
                            d.setMemberRole(null);
                        }
                    }
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
