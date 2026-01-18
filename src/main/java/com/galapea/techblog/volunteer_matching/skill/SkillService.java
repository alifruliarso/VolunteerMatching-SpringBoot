package com.galapea.techblog.volunteer_matching.skill;

import java.util.List;

public interface SkillService {

    List<SkillDTO> findAll();

    List<SkillDTO> findAllByIdIn(List<String> ids);

    SkillDTO get(String id);

    String create(SkillDTO skillDTO);

    void update(String id, SkillDTO skillDTO);

    void delete(String id);

    boolean idExists(String id);

    boolean nameExists(String name);

    void createMultiple(List<SkillDTO> skillDTOs);
}
