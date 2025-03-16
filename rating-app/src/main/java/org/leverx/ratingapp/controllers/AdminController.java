package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.dtos.user.UserDTO;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationService;
import org.leverx.ratingapp.services.comment.CommentService;
import org.leverx.ratingapp.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminController is a REST controller that exposes API endpoints for administrative actions.
 * It provides functionality for managing users, comments, and their states within the system.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path ="admin")
public class AdminController {
    private final UserService userService; // Service for managing users
    private final CommentService commentService; // Service for managing comments
    private final AuthenticationAndRegistrationService authAndRegService; // Service for handling user authentication and registration

    /**
     * Endpoint to confirm a user (either activate or deactivate based on the 'confirm' parameter).
     *
     * @param email the email of the user to confirm
     * @param confirm boolean flag indicating whether to confirm or deny the user
     * @return a ResponseEntity with status 202 and confirmation result
     */
    @GetMapping(path = "confirm")
    public ResponseEntity<String> confirm(
            @RequestParam("email") String email,
            @RequestParam("confirm") Boolean confirm){

        return ResponseEntity.status(202).body(authAndRegService.confirmUser(email,confirm));
    }

    /**
     * Endpoint to retrieve a list of users who have confirmed email.
     *
     * @return a ResponseEntity containing a list of pending users as UserDTO objects
     */
    @GetMapping(path = "users/pending")
    public ResponseEntity<List<UserDTO>> getPendingUsers() {
        return ResponseEntity.ok(userService.getPendingUsers());
    }

    /**
     * Endpoint to retrieve all users in the system.
     *
     * @return a ResponseEntity containing a list of all users as UserDTO objects
     */
    @GetMapping(path= "users")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers(false,true));
    }

    /**
     * Endpoint to retrieve a specific user by their user ID.
     *
     * @param user_id the ID of the user to retrieve
     * @return a ResponseEntity containing the requested user as a UserDTO object
     */
    @GetMapping(path= "users/{user_id}")
    public ResponseEntity<UserDTO> getAnyUser(
            @PathVariable Long user_id){
        return ResponseEntity.ok(userService.getUserById(user_id, false));
    }

    /**
     * Endpoint to retrieve all comments associated with a seller based on their seller ID.
     *
     * @param user_id the seller's ID whose comments are to be retrieved
     * @return a ResponseEntity containing a list of comments for the seller as CommentResponseDTO objects
     */
    @GetMapping(path ="users/{user_id}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getAllCommentsBySellerId(
            @PathVariable Long user_id){
        return ResponseEntity.ok(commentService.getAllBySellerId(user_id,true));
    }

    /**
     * Endpoint to retrieve all comments across all users in the system.
     *
     * @return a ResponseEntity containing a list of all comments as CommentResponseDTO objects
     */
    @GetMapping(path = "users/comments")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments(){
        return ResponseEntity.ok(commentService.getAll());
    }
    
    /**
     * Endpoint to retrieve a specific comment by its comment ID for a specific seller.
     *
     * @param user_id the seller's ID whose comment is to be retrieved
     * @param comment_id the ID of the comment to retrieve
     * @return a ResponseEntity containing the requested comment as a CommentResponseDTO object
     */
    @GetMapping(path ="users/{user_id}/comments/{comment_id}")
    public ResponseEntity<CommentResponseDTO> getComment(
            @PathVariable Long user_id,
            @PathVariable Long comment_id){
        return ResponseEntity.ok(commentService.getComment(user_id,comment_id,true));
    }

    /**
     * Endpoint to approve or disapprove a comment for a specific seller.
     *
     * @param user_id the seller's ID associated with the comment
     * @param comment_id the ID of the comment to approve or disapprove
     * @param confirm boolean flag indicating whether to approve or disapprove the comment
     * @return a ResponseEntity containing the updated comment as a CommentResponseDTO object
     */
    @PostMapping(path ="users/{user_id}/comments/{comment_id}")
    public ResponseEntity<CommentResponseDTO> approveComment(
            @PathVariable Long user_id,
            @PathVariable Long comment_id,
            @RequestParam("confirm") Boolean confirm){
        return ResponseEntity.status(202).body(commentService.approveComment(user_id,comment_id,confirm));
    }
    /**
     * Endpoint to retrieve all inactive users whi have not confirmed email in the system.
     *
     * @return a ResponseEntity containing a list of inactive users as UserDTO objects
     */
    @GetMapping(path = "users/inactive")
    public ResponseEntity<List<UserDTO>> getInactiveUsers(){
        return ResponseEntity.ok(userService.getInactiveUsers());
    }
}
