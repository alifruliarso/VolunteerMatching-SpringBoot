package com.galapea.techblog.volunteer_matching.opportunity_requirement;

import java.util.List;

public interface OpportunityRequirementService {

    List<OpportunityRequirementDTO> findAll();

    List<OpportunityRequirementDTO> findAllByOpportunityId(String opportunityId);

    OpportunityRequirementDTO get(String id);

    String create(OpportunityRequirementDTO opportunityRequirementDTO);

    void update(String id, OpportunityRequirementDTO opportunityRequirementDTO);

    void delete(String id);

    void replaceAllForOpportunity(String opportunityId, List<OpportunityRequirementDTO> requirements);
}
