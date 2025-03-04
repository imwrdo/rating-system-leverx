package org.leverx.ratingapp.controllers;

import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.auth.AuthenticationResponse;
import org.leverx.ratingapp.dtos.auth.RegistrationRequestDTO;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="auth")
public class RegistrationController {
    private final AuthenticationAndRegistrationService service;

    public RegistrationController(AuthenticationAndRegistrationService service) {
        this.service = service;
    }

    @PostMapping(path="register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequestDTO request){
        return ResponseEntity.ok(service.register(request));
    }
    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token){

        return service.confirmToken(token);
    }

    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequestDTO request){
        return ResponseEntity.ok(service.authenticate(request));
    }
}
