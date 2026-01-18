package com.galapea.techblog.volunteer_matching.registration;

/**
 * Exception thrown when a user attempts to register for an opportunity they are already registered for.
 */
public class AlreadyRegisteredException extends RuntimeException {
    private final String userId;
    private final String opportunityId;

    public AlreadyRegisteredException(final String userId, final String opportunityId) {
        super("User " + userId + " is already registered for opportunity " + opportunityId);
        this.userId = userId;
        this.opportunityId = opportunityId;
    }

    public String getUserId() {
        return userId;
    }

    public String getOpportunityId() {
        return opportunityId;
    }
}
