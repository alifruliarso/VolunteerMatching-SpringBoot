package com.galapea.techblog.volunteer_matching.registration;

public record RegistrationResponse(
        String id,
        String userId,
        String opportunityId,
        String status,
        Boolean isPending,
        String registrationTime,
        String fullName,
        String opportunityTitle) {}
