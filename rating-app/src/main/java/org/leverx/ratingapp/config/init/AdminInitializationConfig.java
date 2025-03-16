package org.leverx.ratingapp.config.init;

import lombok.RequiredArgsConstructor;
import org.leverx.ratingapp.models.entities.User;
import org.leverx.ratingapp.models.enums.Role;
import org.leverx.ratingapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class responsible for initializing the admin user during application startup.
 */
@Configuration
@RequiredArgsConstructor
public class AdminInitializationConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /**
     * Creates an admin user if one does not already exist.
     *
     * @param adminEmail    The email of the admin user, injected from application properties.
     * @param adminPassword The password of the admin user, injected from application properties.
     * @return A CommandLineRunner that initializes the admin user upon application startup.
     */
    @Bean
    public CommandLineRunner initializeAdmin(
            @Value("${admin.email}") String adminEmail,
            @Value("${admin.password}") String adminPassword
    ) {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .firstName("Admin")
                        .lastName("Admin")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
