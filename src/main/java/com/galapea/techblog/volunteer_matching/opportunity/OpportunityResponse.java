package com.galapea.techblog.volunteer_matching.opportunity;

import java.time.LocalDateTime;
import java.util.List;

public record OpportunityResponse(
        String id,
        String title,
        String description,
        String address,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long totalSlots,
        Long availableSlots,
        String orgId,
        String orgName,
        String registrationId,
        Boolean registered,
        LocalDateTime registrationTime,
        String registrationStatus,
        List<OpportunitySkill> skills) {}
