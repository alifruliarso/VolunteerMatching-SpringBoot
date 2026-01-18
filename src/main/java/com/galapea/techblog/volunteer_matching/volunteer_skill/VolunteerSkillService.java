package com.galapea.techblog.volunteer_matching.volunteer_skill;

import java.util.List;

public interface VolunteerSkillService {

    List<VolunteerSkillDTO> findAll();

    VolunteerSkillDTO get(String id);

    String create(VolunterSkillAddRequest volunteerSkillDTO);

    void update(String id, VolunteerSkillDTO volunteerSkillDTO);

    void delete(String id);

    boolean idExists(String id);

    public List<VolunteerSkillDTO> findAllByUserId(String userId);
}
