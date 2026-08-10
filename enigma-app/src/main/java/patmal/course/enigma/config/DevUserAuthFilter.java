package patmal.course.enigma.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * LOCAL DEV ONLY (never active in the prod profile): authenticates requests by
 * trusting an X-Dev-User header carrying a user UUID. Lets you exercise the
 * /api endpoints as any user without a real Supabase JWT.
 */
@Component
@Profile("!prod")
public class DevUserAuthFilter extends OncePerRequestFilter {
    public static final String HEADER = "X-Dev-User";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String devUser = request.getHeader(HEADER);
        if (devUser != null && !devUser.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UUID userId = UUID.fromString(devUser.trim());
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                userId.toString(), "N/A",
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))));
            } catch (IllegalArgumentException ignored) {
                // Malformed UUID -> stay unauthenticated; the chain returns 401/403
            }
        }
        filterChain.doFilter(request, response);
    }
}
