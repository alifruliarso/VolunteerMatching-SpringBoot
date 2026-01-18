package com.galapea.techblog.volunteer_matching.volunteer_skill;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class VolunteerSkillDTO {

    @Size(max = 255)
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

    private String skillName;

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

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

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(final String skillId) {
        this.skillId = skillId;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(final VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}
