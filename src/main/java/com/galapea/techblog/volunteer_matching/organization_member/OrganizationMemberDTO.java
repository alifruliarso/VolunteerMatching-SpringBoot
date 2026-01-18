package com.galapea.techblog.volunteer_matching.organization_member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrganizationMemberDTO {

    @Size(max = 255)
    @OrganizationMemberIdValid
    private String id;

    @NotNull
    @Size(max = 255)
    private String userId;

    @NotNull
    @Size(max = 255)
    private String orgId;

    @NotNull
    private MemberRole memberRole;

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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(final String orgId) {
        this.orgId = orgId;
    }

    public MemberRole getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(final MemberRole memberRole) {
        this.memberRole = memberRole;
    }
}
