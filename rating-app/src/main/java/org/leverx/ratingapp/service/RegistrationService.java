package org.leverx.ratingapp.service;

import jakarta.transaction.Transactional;
import org.leverx.ratingapp.dto.RegistrationRequestDTO;
import org.leverx.ratingapp.entity.User;
import org.leverx.ratingapp.entity.token.ConfirmationToken;
import org.leverx.ratingapp.repository.ConfirmationTokenRepository;
import org.springframework.stereotype.Service;

import org.leverx.ratingapp.enums.Role;

import java.time.LocalDateTime;

@Service
public class RegistrationService {
    private final EmailValidatorService emailValidatorService;
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;

    public RegistrationService(EmailValidatorService emailValidatorService,
                               UserService userService,
                               ConfirmationTokenService confirmationTokenService) {
        this.emailValidatorService = emailValidatorService;
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
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

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getConfirmationToken(token)
                .orElseThrow(()->
                        new IllegalArgumentException(String
                                .format("Token %s not found", token)
                        ));
        if(confirmationToken.getConfirmationDateTime() != null) {
            throw new IllegalArgumentException("Email already confirmed");
        }
        LocalDateTime expirationDateTime = confirmationToken.getExpiryDateTime();

        if(expirationDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token is expired");
        }

        confirmationTokenService.setConfirmationDate(token);
        userService.enableUser(
                confirmationToken.getUser()
                .getEmail());

        return "confirmed";
    }
}
