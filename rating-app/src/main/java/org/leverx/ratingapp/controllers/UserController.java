package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.user.UserRankingDTO;
import org.leverx.ratingapp.dtos.user.UserDTO;
import org.leverx.ratingapp.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path ="users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllActivatedUsers() {
        List<UserDTO> users = userService.getAllActivatedUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping(path ="{seller_id}")
    public ResponseEntity<UserDTO> getActiveUser(@PathVariable Long seller_id) {
        return ResponseEntity.ok(userService.getActiveUser(seller_id));
    }

    @GetMapping(path = "rating")
    public ResponseEntity<List<UserRankingDTO>> getUsersRating(
            @RequestParam(required = false) String gameName,
            @RequestParam(required = false) Long ratingLimit
    ) {
        return ResponseEntity.ok(userService.getUserRating(gameName,ratingLimit));
    }
}
