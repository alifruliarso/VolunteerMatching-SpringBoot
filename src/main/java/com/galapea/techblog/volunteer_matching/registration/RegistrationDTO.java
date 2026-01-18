package com.galapea.techblog.volunteer_matching.registration;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class RegistrationDTO {

    @Size(max = 255)
    @RegistrationIdValid
    private String id;

    @NotNull
    @Size(max = 255)
    private String userId;

    @NotNull
    @Size(max = 255)
    private String opportunityId;

    @NotNull
    private RegistrationStatus status;

    private LocalDateTime registrationTime;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(final String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(final RegistrationStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(final LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }
}
