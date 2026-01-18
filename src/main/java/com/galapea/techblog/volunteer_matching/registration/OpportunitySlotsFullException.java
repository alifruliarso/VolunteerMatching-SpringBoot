package com.galapea.techblog.volunteer_matching.registration;

/**
 * Exception thrown when an opportunity has no available slots for registration.
 */
public class OpportunitySlotsFullException extends RuntimeException {
    private final String opportunityId;
    private final Long slotsTotal;
    private final Long registeredCount;

    public OpportunitySlotsFullException(
            final String opportunityId, final Long slotsTotal, final Long registeredCount) {
        super("Opportunity " + opportunityId + " is full. Total slots: " + slotsTotal + ", Registered: "
                + registeredCount);
        this.opportunityId = opportunityId;
        this.slotsTotal = slotsTotal;
        this.registeredCount = registeredCount;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public Long getSlotsTotal() {
        return slotsTotal;
    }

    public Long getRegisteredCount() {
        return registeredCount;
    }
}
