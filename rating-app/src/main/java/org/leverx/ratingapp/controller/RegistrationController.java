package org.leverx.ratingapp.controller;

import org.leverx.ratingapp.dto.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dto.auth.AuthenticationResponse;
import org.leverx.ratingapp.dto.auth.RegistrationRequestDTO;
import org.leverx.ratingapp.service.auth.AuthenticationAndRegistrationService;
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
