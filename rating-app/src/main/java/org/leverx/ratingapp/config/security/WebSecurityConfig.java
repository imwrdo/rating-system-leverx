package org.leverx.ratingapp.config.security;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security settings.
 * Defines security filters, authentication mechanisms, and access control rules.
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configures security settings, including authentication, session management,
     * and access control rules.
     *
     * @param httpSecurity The HTTP security configuration.
     * @return The configured {@link SecurityFilterChain}.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // Disable CSRF protection since we are using stateless authentication (JWT)
                .csrf(AbstractHttpConfigurer::disable)
                // Configure session management to be stateless (no session storage)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configure exception handling for unauthorized access
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                // Define access control rules
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/users/*/comments").permitAll()
                        .requestMatchers(HttpMethod.POST,"/users/*/comments").permitAll()
                        .requestMatchers(HttpMethod.POST,"/users/*/comments/optional-seller").permitAll()
                        .requestMatchers("/admin/**").hasAuthority(Role.ADMIN.getValueOfRole())
                        .anyRequest().authenticated()
                )
                // Set the authentication provider
                .authenticationProvider(authenticationProvider)
                // Add JWT filter before Spring Security's default authentication filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
