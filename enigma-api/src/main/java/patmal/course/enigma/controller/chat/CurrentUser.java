package patmal.course.enigma.controller.chat;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Resolves the authenticated user's UUID from the security context.
 * In prod this is the Supabase JWT "sub" claim; in local dev it comes from
 * the X-Dev-User header (see DevUserAuthFilter in enigma-app).
 */
public final class CurrentUser {
    private CurrentUser() {
    }

    public static UUID id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new SecurityException("Not authenticated");
        }
        try {
            return UUID.fromString(auth.getName());
        } catch (IllegalArgumentException e) {
            throw new SecurityException("Authenticated principal is not a user UUID");
        }
    }
}
