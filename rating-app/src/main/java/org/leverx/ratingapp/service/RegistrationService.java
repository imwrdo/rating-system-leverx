package org.leverx.ratingapp.service;

import org.leverx.ratingapp.dto.RegistrationRequestDTO;
import org.leverx.ratingapp.entity.User;
import org.springframework.stereotype.Service;

import org.leverx.ratingapp.enums.Role;

@Service
public class RegistrationService {
    private final EmailValidatorService emailValidatorService;
    private final UserService userService;

    public RegistrationService(EmailValidatorService emailValidatorService, UserService userService) {
        this.emailValidatorService = emailValidatorService;
        this.userService = userService;
    }

    public String register(RegistrationRequestDTO registrationRequestDTO) {
        boolean isValidEmail = emailValidatorService
                .test(registrationRequestDTO.email());
        if(!isValidEmail) {
            throw new IllegalArgumentException("Invalid email");
        }
        return userService.signUpUser(
                new User(
                        registrationRequestDTO.first_name(),
                        registrationRequestDTO.last_name(),
                        registrationRequestDTO.password(),
                        registrationRequestDTO.email(),
                        Role.SELLER
                )
        );
    }
}
