package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.user.UserRankingDTO;
import org.leverx.ratingapp.dtos.user.UserDTO;
import org.leverx.ratingapp.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * UserController handles REST-ful API endpoints related to user information,
 * including retrieving activated users, specific user data, and user rankings.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path ="users")
public class UserController {
    private final UserService userService; // Service to handle user-related operations

    /**
     * Endpoint to get all activated users.
     *
     * @return a {@link ResponseEntity} containing a list of {@link UserDTO} objects for all activated users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllActivatedUsers() {
        // Fetch and return a list of activated users (excluding inactive or unapproved users)
        return ResponseEntity.ok(userService.getAllUsers(true,false));
    }

    /**
     * Endpoint to get a specific active user by their ID.
     *
     * @param seller_id the ID of the user to retrieve
     * @return {@link ResponseEntity} containing a {@link UserDTO} object for the specified user
     */
    @GetMapping(path ="{seller_id}")
    public ResponseEntity<UserDTO> getActiveUser(@PathVariable Long seller_id) {
        // Fetch and return a specific user by ID, ensuring the user has seller role
        return ResponseEntity.ok(userService.getUserById(seller_id,true));
    }

    /**
     * Endpoint to get a list of users ranked based on their ratings.
     *
     * @param gameName optional query parameter for filtering users by a specific game
     * @param ratingLimit optional query parameter for limiting the number of users in the ranking
     * @return a {@link ResponseEntity} containing a list of {@link UserRankingDTO} objects, showing user rankings
     */
    @GetMapping(path = "rating")
    public ResponseEntity<List<UserRankingDTO>> getUsersRating(
            @RequestParam(required = false) String gameName, // Optional query parameter to filter by game name
            @RequestParam(required = false) Long ratingLimit // Optional query parameter to limit the number of rankings returned
    ) {
        // Fetch and return user rankings, filtered by game name and limited by ratingLimit if provided
        return ResponseEntity.ok(userService.getUserRating(gameName,ratingLimit));
    }
}
