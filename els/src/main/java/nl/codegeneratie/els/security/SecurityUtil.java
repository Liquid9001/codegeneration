package nl.codegeneratie.els.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private static Claims getClaims() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Claims) auth.getPrincipal();
    }

    public static Long getCurrentUserId() {
        return getClaims().get("userId", Long.class);
    }

    public static String getCurrentUserRole() {
        return getClaims().get("role", String.class);
    }

    public static boolean isEmployeeOrAdmin() {
        String role = getCurrentUserRole();
        return role.equals("EMPLOYEE") || role.equals("ADMIN");
    }
}