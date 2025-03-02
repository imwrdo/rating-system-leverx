package org.leverx.ratingapp.service;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.entity.User;
import org.springframework.stereotype.Service;

import org.leverx.ratingapp.enums.Role;

@Service
public class RegistrationService {
    private final EmailValidator emailValidator;
    private final UserService userService;

    public RegistrationService(EmailValidator emailValidator, UserService userService) {
        this.emailValidator = emailValidator;
        this.userService = userService;
    }

    public String register(RegistrationRequest registrationRequest) {
        boolean isValidEmail = emailValidator
                .test(registrationRequest.email());
        if(!isValidEmail) {
            throw new IllegalArgumentException("Invalid email");
        }
        return userService.signUpUser(
                new User(
                        registrationRequest.first_name(),
                        registrationRequest.last_name(),
                        registrationRequest.password(),
                        registrationRequest.email(),
                        Role.SELLER
                )
        );
    }
}
