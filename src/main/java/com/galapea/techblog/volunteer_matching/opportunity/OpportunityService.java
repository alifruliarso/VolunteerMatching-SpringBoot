package com.galapea.techblog.volunteer_matching.opportunity;

import java.util.List;

public interface OpportunityService {
    String nextId();

    List<OpportunityDTO> findAll();

    List<OpportunityDTO> findAllByOrgId(String orgId);

    OpportunityDTO get(String id);

    java.util.Optional<OpportunityDTO> getOneById(String id);

    String create(OpportunityDTO opportunityDTO);

    void update(String id, OpportunityDTO opportunityDTO);

    void delete(String id);

    boolean idExists(String id);
}
