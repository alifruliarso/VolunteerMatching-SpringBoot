package com.galapea.techblog.volunteer_matching.registration;

/**
 * Exception thrown when a user lacks a mandatory skill required for the opportunity.
 */
public class MissingMandatorySkillException extends RuntimeException {
    private final String userId;
    private final String opportunityId;
    private final String skillId;
    private final String skillName;

    public MissingMandatorySkillException(
            final String userId, final String opportunityId, final String skillId, final String skillName) {
        super("User " + userId + " lacks mandatory skill: " + skillName + " for opportunity " + opportunityId);
        this.userId = userId;
        this.opportunityId = opportunityId;
        this.skillId = skillId;
        this.skillName = skillName;
    }

    public String getUserId() {
        return userId;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public String getSkillId() {
        return skillId;
    }

    public String getSkillName() {
        return skillName;
    }
}
