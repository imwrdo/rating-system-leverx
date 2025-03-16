package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.auth.AuthenticationResponseDTO;
import org.leverx.ratingapp.dtos.auth.PasswordResetRequestDTO;
import org.leverx.ratingapp.dtos.auth.registration.RegistrationRequestDTO;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RegistrationAndAuthenticationController is a REST controller that handles authentication, registration,
 * and password management requests for users.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path="auth")
public class RegistrationAndAuthenticationController {
    private final AuthenticationAndRegistrationService service; // Service for handling authentication and registration operations

    /**
     * Endpoint for registering a new user.
     *
     * @param request the registration request containing user details
     * @return a {@link ResponseEntity}  containing an {@link AuthenticationResponseDTO}
     * with registration information
     */
    @PostMapping(path="register")
    public ResponseEntity<AuthenticationResponseDTO> register(
            @RequestBody RegistrationRequestDTO request){
        return ResponseEntity.ok(service.register(request));
    }

    /**
     * Endpoint to confirm user email address using a confirmation token.
     *
     * @param token the confirmation token to verify the email address
     * @return a {@link ResponseEntity}  containing a confirmation message
     */
    @GetMapping(path = "confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token){
        return ResponseEntity.ok(service.confirmEmail(token));
    }

    /**
     * Endpoint to authenticate an existing user with email and password.
     *
     * @param request the authentication request containing user credentials
     * @return a {@link ResponseEntity} containing an {@link AuthenticationResponseDTO} with authentication details
     */
    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @RequestBody AuthenticationRequestDTO request){
        return ResponseEntity.status(202).body(service.authenticate(request));
    }

    /**
     * Endpoint to initiate a password reset for a user who has forgotten their password.
     *
     * @param request the request containing the email of the user who wants to reset their password
     * @return a {@link ResponseEntity} containing an {@link AuthenticationResponseDTO}
     * with password reset instructions
     */
    @PostMapping(path = "forgot_password")
    public ResponseEntity<AuthenticationResponseDTO> forgotPassword(
            @RequestBody AuthenticationRequestDTO request) {
        return ResponseEntity.ok(service.initiatePasswordReset(request.email()));
    }

    /**
     * Endpoint to reset the user's password with a new password.
     *
     * @param request the request containing the new password and the reset token
     * @return a {@link ResponseEntity} containing an {@link AuthenticationResponseDTO} with reset status
     */
    @PostMapping(path = "reset")
    public ResponseEntity<AuthenticationResponseDTO> reset(
            @RequestBody PasswordResetRequestDTO request) {
        return ResponseEntity.ok(service.resetPassword(request));
    }

    /**
     * Endpoint to verify the password reset code sent to the user's email.
     *
     * @param email the email address of the user who is resetting their password
     * @param code the reset code to verify
     * @return a {@link ResponseEntity} containing an {@link AuthenticationResponseDTO} with the verification result
     */
    @GetMapping(path = "check_code")
    public ResponseEntity<AuthenticationResponseDTO> checkCode(
            @RequestParam String email,
            @RequestParam String code) {
        return ResponseEntity.ok(service.verifyResetCode(email, code));
    }
}
