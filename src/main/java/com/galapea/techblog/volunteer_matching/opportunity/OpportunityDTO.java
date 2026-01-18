package com.galapea.techblog.volunteer_matching.opportunity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class OpportunityDTO {

    private String id;

    @NotNull
    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String description;

    @Size(max = 255)
    private String address;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    private Long slotsTotal;

    @NotNull
    @Size(max = 255)
    private String orgId;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(final LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(final LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getSlotsTotal() {
        return slotsTotal;
    }

    public void setSlotsTotal(final Long slotsTotal) {
        this.slotsTotal = slotsTotal;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(final String orgId) {
        this.orgId = orgId;
    }
}
