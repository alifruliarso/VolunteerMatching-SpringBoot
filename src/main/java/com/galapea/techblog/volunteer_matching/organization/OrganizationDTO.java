package com.galapea.techblog.volunteer_matching.organization;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrganizationDTO {

    @Size(max = 255)
    @OrganizationIdValid
    private String id;

    @NotNull
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String websiteUrl;

    @NotNull
    @Size(max = 255)
    private String adminUserId;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(final String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(final String adminUserId) {
        this.adminUserId = adminUserId;
    }
}
