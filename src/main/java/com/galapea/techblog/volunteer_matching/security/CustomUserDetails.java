package com.galapea.techblog.volunteer_matching.security;

import com.galapea.techblog.volunteer_matching.organization.OrganizationDTO;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {
    private final String userId;
    private final List<OrganizationDTO> organizations;

    public CustomUserDetails(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String userId,
            List<OrganizationDTO> organizations) {
        super(username, password, authorities);
        this.userId = userId;
        this.organizations = organizations;
    }

    public String getUserId() {
        return this.userId;
    }

    public List<OrganizationDTO> getOrganizations() {
        return this.organizations;
    }
}
