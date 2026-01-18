package com.galapea.techblog.volunteer_matching.opportunity_requirement;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OpportunityRequirementDTO {

    private String id;

    @NotNull
    @Size(max = 255)
    private String opportunityId;

    @NotNull
    @Size(max = 255)
    private String skillId;

    @JsonProperty("isMandatory")
    private Boolean isMandatory;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean isIsMandatory() {
        return this.isMandatory;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(final String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(final String skillId) {
        this.skillId = skillId;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(final Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
}
