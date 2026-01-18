package com.galapea.techblog.volunteer_matching.user;

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
public class UserGridDBService implements UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbClient gridDbClient;
    private final String TBL_NAME = "VoMaUsers";

    public UserGridDBService(final GridDbClient gridDbClient) {
        this.gridDbClient = gridDbClient;
    }

    public void createTable() {
        List<GridDbColumn> columns = List.of(
                new GridDbColumn("id", "STRING", Set.of("TREE")),
                new GridDbColumn("email", "STRING", Set.of("TREE")),
                new GridDbColumn("fullName", "STRING"),
                new GridDbColumn("role", "STRING"));

        GridDbContainerDefinition containerDefinition = GridDbContainerDefinition.build(TBL_NAME, columns);
        this.gridDbClient.createContainer(containerDefinition);
    }

    @Override
    public List<UserDTO> findAll() {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(50L).sort("id ASC").build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        return response.getRows().stream().map(row -> mapToDTO(row)).collect(Collectors.toList());
    }

    private UserDTO mapToDTO(List<Object> row) {
        UserDTO dto = new UserDTO();
        dto.setId((String) row.get(0));
        dto.setEmail((String) row.get(1));
        dto.setFullName((String) row.get(2));
        String roleStr = (String) row.get(3);
        if (roleStr != null) {
            dto.setRole(UserRole.valueOf(roleStr));
        }
        return dto;
    }

    @Override
    public UserDTO get(final String id) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("id == \'" + id + "\'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            throw new NotFoundException("User not found with id: " + id);
        }
        return response.getRows().stream()
                .findFirst()
                .map(row -> mapToDTO(row))
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    public String nextId() {
        return TsidCreator.getTsid().format("usr_%s");
    }

    @Override
    public String create(final UserDTO userDTO) {
        String id = userDTO.getId() != null && !userDTO.getId().isBlank() ? userDTO.getId() : nextId();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(userDTO.getEmail())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(userDTO.getFullName())).append("\"");
        sb.append(", ");
        sb.append(
                userDTO.getRole() == null
                        ? "null"
                        : "\"" + escapeString(userDTO.getRole().name()) + "\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Create User: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
        return id;
    }

    @Override
    public void update(final String id, final UserDTO userDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(userDTO.getEmail())).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(userDTO.getFullName())).append("\"");
        if (userDTO.getRole() != null) {
            sb.append(", ");
            sb.append("\"" + escapeString(userDTO.getRole().name()) + "\"");
        }
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Update User: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
    }

    @Override
    public void delete(final String id) {
        throw new UnsupportedOperationException("GridDB User service not implemented");
    }

    @Override
    public boolean idExists(final String id) {
        UserDTO user = get(id);
        return user != null;
    }

    public Optional<UserDTO> getOneByEmail(final String email) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("email == \'" + email + "\'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        UserDTO userDTO = response.getRows().stream()
                .findFirst()
                .map(row -> mapToDTO(row))
                .orElse(null);
        return Optional.ofNullable(userDTO);
    }

    @Override
    public boolean emailExists(final String email) {
        Optional<UserDTO> userOpt = getOneByEmail(email);
        return userOpt.isPresent();
    }

    private String escapeString(String input) {
        if (input == null) return null;
        return input.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"");
    }
}
