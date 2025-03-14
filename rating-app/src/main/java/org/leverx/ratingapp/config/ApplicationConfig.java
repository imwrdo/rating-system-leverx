package org.leverx.ratingapp.config;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for application-level security components.
 * Defines authentication and password encoding mechanisms.
 */
@Configuration
@AllArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;

    /**
     * Provides a {@link UserDetailsService} to load user details by username (email).
     *
     * @return A lambda function that fetches user details from the database.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Configures the authentication provider using {@link DaoAuthenticationProvider}.
     * It integrates the custom {@link UserDetailsService} and password encoder.
     *
     * @param passwordEncoder The password encoder used for authentication.
     * @return The configured authentication provider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Provides an {@link AuthenticationManager} to handle authentication requests.
     *
     * @param authenticationConfiguration Spring Security's authentication configuration.
     * @return The authentication manager.
     * @throws Exception If an error occurs during initialization.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    /**
     * Defines the {@link BCryptPasswordEncoder} bean using BCrypt hashing algorithm.
     *
     * @return An instance of {@link BCryptPasswordEncoder}.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
