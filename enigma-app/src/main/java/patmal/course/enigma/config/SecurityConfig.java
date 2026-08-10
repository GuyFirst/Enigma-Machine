package patmal.course.enigma.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Security is profile-driven:
 *  - default/local: /api/** requires auth, provided by DevUserAuthFilter (X-Dev-User header)
 *  - prod:          /api/** requires a valid Supabase JWT (Bearer token)
 * The legacy course endpoints under /enigma/** stay open in both profiles.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Profile("!prod")
    public SecurityFilterChain devFilterChain(HttpSecurity http, DevUserAuthFilter devUserAuthFilter)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(devUserAuthFilter, AnonymousAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Profile("prod")
    public SecurityFilterChain prodFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    /**
     * Supabase JWT decoder. Newer Supabase projects publish asymmetric signing
     * keys via JWKS (set SUPABASE_JWKS_URI); older projects sign with the
     * shared HS256 "JWT secret" (set SUPABASE_JWT_SECRET). Either works.
     */
    @Bean
    @Profile("prod")
    public JwtDecoder jwtDecoder(@Value("${app.auth.jwks-uri:}") String jwksUri,
                                 @Value("${app.auth.jwt-secret:}") String jwtSecret) {
        if (jwksUri != null && !jwksUri.isBlank()) {
            return NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
        }
        if (jwtSecret != null && !jwtSecret.isBlank()) {
            return NimbusJwtDecoder.withSecretKey(
                    new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")).build();
        }
        throw new IllegalStateException(
                "prod profile requires SUPABASE_JWKS_URI or SUPABASE_JWT_SECRET to be set");
    }
}
