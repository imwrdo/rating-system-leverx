package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.user.UserDTO;
import org.leverx.ratingapp.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path ="admin")
public class AdminController {
    private final UserService userService;

    @GetMapping(path= "users")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers(false));
    }

    @GetMapping(path= "users/{user_id}")
    public ResponseEntity<UserDTO> getAnyUser(
            @PathVariable Long user_id
    ){
        return ResponseEntity.ok(userService.getUserById(user_id, false));
    }

}
