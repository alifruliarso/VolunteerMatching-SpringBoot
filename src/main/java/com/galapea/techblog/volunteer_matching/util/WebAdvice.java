package com.galapea.techblog.volunteer_matching.util;

import com.galapea.techblog.volunteer_matching.security.SecurityExpressions;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Provide attributes available in all templates.
 */
@ControllerAdvice
public class WebAdvice {

    @ModelAttribute("requestUri")
    public String getRequestUri(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("roles")
    public Map<String, String> getRoles() {
        Map<String, String> roles = new LinkedHashMap<>();
        roles.put("ADMIN", SecurityExpressions.ROLE_ADMIN);
        roles.put("ORGANIZER", SecurityExpressions.ROLE_ORGANIZER);
        roles.put("VOLUNTEER", SecurityExpressions.ROLE_VOLUNTEER);
        return roles;
    }

    @ModelAttribute("hasAdmin")
    public boolean hasAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_" + SecurityExpressions.ROLE_ADMIN));
    }

    @ModelAttribute("hasOrganizer")
    public boolean hasOrganizer(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_" + SecurityExpressions.ROLE_ORGANIZER));
    }

    @ModelAttribute("hasVolunteer")
    public boolean hasVolunteer(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_" + SecurityExpressions.ROLE_VOLUNTEER));
    }
}
