package com.galapea.techblog.volunteer_matching.opportunity_requirement;

import java.util.ArrayList;
import java.util.List;

public class OpportunityRequirementForm {
    private List<OpportunityRequirementDTO> requirements = new ArrayList<>();

    public OpportunityRequirementForm() {}

    public OpportunityRequirementForm(List<OpportunityRequirementDTO> requirements) {
        this.requirements = requirements;
    }

    public List<OpportunityRequirementDTO> getRequirements() {
        return this.requirements;
    }

    public void setRequirements(List<OpportunityRequirementDTO> requirements) {
        this.requirements = requirements;
    }

    public OpportunityRequirementForm requirements(List<OpportunityRequirementDTO> requirements) {
        setRequirements(requirements);
        return this;
    }
}
