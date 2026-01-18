package com.galapea.techblog.volunteer_matching.volunteer_skill;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class VolunterSkillAddRequest {

    private String id;

    @NotNull
    @Size(max = 255)
    private String userId;

    @NotNull
    @Size(max = 255)
    private String skillId;

    @NotNull
    private LocalDateTime expiryDate;

    private VerificationStatus verificationStatus;

    public VolunterSkillAddRequest() {}

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSkillId() {
        return this.skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public LocalDateTime getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public VerificationStatus getVerificationStatus() {
        return this.verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
