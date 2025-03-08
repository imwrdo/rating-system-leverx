package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.auth.AuthenticationResponseDTO;
import org.leverx.ratingapp.dtos.auth.PasswordResetRequestDTO;
import org.leverx.ratingapp.dtos.auth.registration.RegistrationRequestDTO;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path="auth")
public class RegistrationController {
    private final AuthenticationAndRegistrationService service;

    @PostMapping(path="register")
    public ResponseEntity<AuthenticationResponseDTO> register(
            @RequestBody RegistrationRequestDTO request){
        return ResponseEntity.ok(service.register(request));
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token){

        return ResponseEntity.ok(service.confirmToken(token));
    }

    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @RequestBody AuthenticationRequestDTO request){
        return ResponseEntity.status(202).body(service.authenticate(request));
    }

    @PostMapping(path = "forgot_password")
    public ResponseEntity<AuthenticationResponseDTO> forgotPassword(
            @RequestBody AuthenticationRequestDTO request) {
        return ResponseEntity.ok(service.initiatePasswordReset(request.email()));
    }

    @PostMapping(path = "reset")
    public ResponseEntity<AuthenticationResponseDTO> reset(
            @RequestBody PasswordResetRequestDTO request) {
        return ResponseEntity.ok(service.resetPassword(request));
    }

    @GetMapping(path = "check_code")
    public ResponseEntity<AuthenticationResponseDTO> checkCode(
            @RequestParam String email,
            @RequestParam String code) {
        return ResponseEntity.ok(service.verifyResetCode(email, code));
    }
}
