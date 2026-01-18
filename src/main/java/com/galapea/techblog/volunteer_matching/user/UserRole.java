package com.galapea.techblog.volunteer_matching.user;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    VOLUNTEER,
    ADMIN,
    ORGANIZER;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
