package com.galapea.techblog.volunteer_matching.security;

public final class SecurityExpressions {

    // ===== Role Names =====
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ORGANIZER = "ORGANIZER";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_VOLUNTEER = "VOLUNTEER";

    // ===== Single Role Checks =====
    public static final String ADMIN_ONLY = "hasRole('" + ROLE_ADMIN + "')";
    public static final String ORGANIZER_ONLY = "hasRole('" + ROLE_ORGANIZER + "')";
    public static final String USER_ONLY = "hasRole('" + ROLE_USER + "')";
    public static final String VOLUNTEER_ONLY = "hasRole('" + ROLE_VOLUNTEER + "')";

    // ===== Multiple Role Checks =====
    public static final String ADMIN_OR_ORGANIZER = "hasAnyRole('" + ROLE_ADMIN + "', '" + ROLE_ORGANIZER + "')";

    // ===== Complex Expressions =====
    public static final String AUTHENTICATED_USER = "isAuthenticated()";
    public static final String ADMIN_AUTHENTICATED = "hasRole('" + ROLE_ADMIN + "') and isAuthenticated()";

    private SecurityExpressions() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
