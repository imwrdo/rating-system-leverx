package org.leverx.ratingapp.controller;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dto.RegistrationRequestDTO;
import org.leverx.ratingapp.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="auth")

public class RegistrationController {
    private final RegistrationService registrationService;

     private RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    @PostMapping
    public String register(@RequestBody RegistrationRequestDTO request){
        return registrationService.register(request);
    }
    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token){
        return registrationService.confirmToken(token);
    }
}
