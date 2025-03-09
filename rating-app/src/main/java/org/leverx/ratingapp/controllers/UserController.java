package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.user.UserRankingDTO;
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

    @GetMapping(path = "ranking")
    public ResponseEntity<List<UserRankingDTO>> getRankingUsers() {
        return ResponseEntity.ok(userService.getUserRanking());
    }
}
