package org.leverx.ratingapp.controller;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dto.RegistrationRequestDTO;
import org.leverx.ratingapp.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="auth")
@AllArgsConstructor
public class RegistrationController {
    private RegistrationService registrationService;

    @Autowired
    private void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    @PostMapping
    public String register(@RequestBody RegistrationRequestDTO request){
        return registrationService.register(request);
    }
}
