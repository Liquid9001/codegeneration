package nl.codegeneratie.els.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Claims claims = (Claims) auth.getDetails();
        return claims.get("userId", Integer.class).longValue();
    }

    public static String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Claims claims = (Claims) auth.getDetails();
        return claims.get("role", String.class);
    }

    public static boolean isEmployeeOrAdmin() {
        String role = getCurrentUserRole();
        return role.equals("EMPLOYEE") || role.equals("ADMIN");
    }
}