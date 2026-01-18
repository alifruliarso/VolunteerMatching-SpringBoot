package com.galapea.techblog.volunteer_matching.security;

import com.galapea.techblog.volunteer_matching.organization.OrganizationDTO;
import com.galapea.techblog.volunteer_matching.organization.OrganizationService;
import com.galapea.techblog.volunteer_matching.user.UserDTO;
import com.galapea.techblog.volunteer_matching.user.UserRole;
import com.galapea.techblog.volunteer_matching.user.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final OrganizationService organizationService;

    public CustomUserDetailsService(final UserService userService, final OrganizationService organizationService) {
        this.userService = userService;
        this.organizationService = organizationService;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) {
        Optional<UserDTO> userOpt = userService.getOneByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else {
            UserDTO userDTO = userOpt.get();
            List<OrganizationDTO> orgs = new ArrayList<>();
            List<SimpleGrantedAuthority> authorities = new ArrayList<>(Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_" + userDTO.getRole().name())));
            Optional<OrganizationDTO> org = organizationService.getOneByAdminUserId(userDTO.getId());
            if (org.isPresent()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + UserRole.ORGANIZER.name()));
                orgs.add(org.get());
            }
            UserDetails userDetails =
                    new CustomUserDetails(userDTO.getEmail(), "{noop}123", authorities, userDTO.getId(), orgs);
            return userDetails;
        }
    }
}
