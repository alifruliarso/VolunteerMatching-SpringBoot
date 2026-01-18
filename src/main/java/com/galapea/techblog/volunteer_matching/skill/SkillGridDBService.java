package com.galapea.techblog.volunteer_matching.skill;

import com.galapea.techblog.volunteer_matching.griddb.GridDbClient;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsRequest;
import com.galapea.techblog.volunteer_matching.griddbwebapi.AcquireRowsResponse;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbCloudSQLStmt;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbColumn;
import com.galapea.techblog.volunteer_matching.griddbwebapi.GridDbContainerDefinition;
import com.galapea.techblog.volunteer_matching.griddbwebapi.SQLSelectResponse;
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
public class SkillGridDBService implements SkillService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbClient gridDbClient;
    private final String TBL_NAME = "VoMaSkills";

    public SkillGridDBService(final GridDbClient gridDbClient) {
        this.gridDbClient = gridDbClient;
    }

    public void createTable() {
        List<GridDbColumn> columns = List.of(
                new GridDbColumn("id", "STRING", Set.of("TREE")), new GridDbColumn("name", "STRING", Set.of("TREE")));

        GridDbContainerDefinition containerDefinition = GridDbContainerDefinition.build(TBL_NAME, columns);
        this.gridDbClient.createContainer(containerDefinition);
    }

    @Override
    public List<SkillDTO> findAll() {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(50L).sort("id ASC").build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        return response.getRows().stream()
                .map(row -> {
                    SkillDTO dto = new SkillDTO();
                    dto.setId((String) row.get(0));
                    dto.setName((String) row.get(1));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SkillDTO get(final String id) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("id == '" + id + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            throw new NotFoundException("Skill not found with id: " + id);
        }
        return response.getRows().stream()
                .findFirst()
                .map(row -> {
                    SkillDTO dto = new SkillDTO();
                    dto.setId((String) row.get(0));
                    dto.setName((String) row.get(1));
                    return dto;
                })
                .orElseThrow(() -> new NotFoundException("Skill not found with id: " + id));
    }

    public String nextId() {
        return TsidCreator.getTsid().format("sk_%s");
    }

    @Override
    public String create(final SkillDTO skillDTO) {
        String id = skillDTO.getId() != null && !skillDTO.getId().isBlank() ? skillDTO.getId() : nextId();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(skillDTO.getName())).append("\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Create Skill: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
        return id;
    }

    @Override
    public void update(final String id, final SkillDTO skillDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        sb.append("\"").append(escapeString(id)).append("\"");
        sb.append(", ");
        sb.append("\"").append(escapeString(skillDTO.getName())).append("\"");
        sb.append("]");
        sb.append("]");
        String result = sb.toString();
        log.info("Update Skill: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
    }

    @Override
    public void delete(final String id) {
        throw new UnsupportedOperationException("GridDB Skill service not implemented");
    }

    @Override
    public boolean idExists(final String id) {
        SkillDTO s = get(id);
        return s != null;
    }

    @Override
    public boolean nameExists(final String name) {
        return getOneByName(name).isPresent();
    }

    public Optional<SkillDTO> getOneByName(final String name) {
        AcquireRowsRequest requestBody = AcquireRowsRequest.builder()
                .limit(1L)
                .condition("name == '" + name + "'")
                .build();
        AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        SkillDTO dto = response.getRows().stream()
                .findFirst()
                .map(row -> {
                    SkillDTO d = new SkillDTO();
                    d.setId((String) row.get(0));
                    d.setName((String) row.get(1));
                    return d;
                })
                .orElse(null);
        return Optional.ofNullable(dto);
    }

    private String escapeString(String input) {
        if (input == null) return null;
        return input.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"");
    }

    @Override
    public void createMultiple(List<SkillDTO> skillDTOs) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < skillDTOs.size(); i++) {
            SkillDTO skillDTO = skillDTOs.get(i);
            String id = skillDTO.getId() != null && !skillDTO.getId().isBlank() ? skillDTO.getId() : nextId();
            sb.append("[");
            sb.append("\"").append(escapeString(id)).append("\"");
            sb.append(", ");
            sb.append("\"").append(escapeString(skillDTO.getName())).append("\"");
            sb.append("]");
            if (i < skillDTOs.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        String result = sb.toString();
        log.info("Create Multiple Skills: {}", result);
        this.gridDbClient.registerRows(TBL_NAME, result);
    }

    @Override
    public List<SkillDTO> findAllByIdIn(List<String> ids) {
        StringBuilder idLiStringBuilder = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            idLiStringBuilder.append("'").append(escapeString(ids.get(i))).append("'");
            if (i < ids.size() - 1) {
                idLiStringBuilder.append(", ");
            }
        }
        // @formatter:off
        String stmt =
                """
            SELECT id, name \
            FROM "VoMaSkills" \
            WHERE id IN (%s)
        """
                        .formatted(idLiStringBuilder.toString());
        // @formatter:on
        List<GridDbCloudSQLStmt> statementList = List.of(new GridDbCloudSQLStmt(stmt));
        SQLSelectResponse[] response = this.gridDbClient.select(statementList);
        if (response == null || response.length != statementList.size()) {
            log.error(
                    "Response is null or size mismatch. Expected: {}, Actual: {}",
                    statementList.size(),
                    response != null ? response.length : 0);
            return List.of();
        }

        List<List<Object>> results = response[0].getResults();
        if (results.isEmpty()) {
            log.info("No result for find by list of ID");
            return List.of();
        }
        return results.stream()
                .map(row -> {
                    SkillDTO dto = new SkillDTO();
                    dto.setId((String) row.get(0));
                    dto.setName((String) row.get(1));
                    return dto;
                })
                .collect(Collectors.toList());

        // AcquireRowsRequest requestBody =
        //         AcquireRowsRequest.builder().limit(50L).condition("id IN ("+ idLiStringBuilder.toString()
        // +")").sort("id ASC").build();
        // AcquireRowsResponse response = this.gridDbClient.acquireRows(TBL_NAME, requestBody);
        // if (response == null || response.getRows() == null) {
        //     log.error("Failed to acquire rows from GridDB");
        //     return List.of();
        // }
        // return response.getRows().stream()
        //         .map(row -> {
        //             SkillDTO dto = new SkillDTO();
        //             dto.setId((String) row.get(0));
        //             dto.setName((String) row.get(1));
        //             return dto;
        //         })
        //         .collect(Collectors.toList());
    }
}
