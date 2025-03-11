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

@RestController
@AllArgsConstructor
@RequestMapping(path ="admin")
public class AdminController {
    private final UserService userService;
    private final CommentService commentService;
    private final AuthenticationAndRegistrationService authAndRegService;

    @GetMapping(path = "confirm")
    public ResponseEntity<String> confirm(
            @RequestParam("email") String email,
            @RequestParam("confirm") Boolean confirm){

        return ResponseEntity.status(202).body(authAndRegService.confirmUser(email,confirm));
    }

    @GetMapping(path = "users/pending")
    public ResponseEntity<List<UserDTO>> getPendingUsers() {
        return ResponseEntity.ok(userService.getPendingUsers());
    }

    @GetMapping(path= "users")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers(false,true));
    }

    @GetMapping(path= "users/{user_id}")
    public ResponseEntity<UserDTO> getAnyUser(
            @PathVariable Long user_id
    ){
        return ResponseEntity.ok(userService.getUserById(user_id, false));
    }

    @GetMapping(path ="users/{seller_id}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getAllCommentsBySellerId(
            @PathVariable Long seller_id){
        return ResponseEntity.ok(commentService.getAllBySellerId(seller_id,true));
    }

    @GetMapping(path = "users/comments")
    public ResponseEntity<List<CommentResponseDTO>> getAllComments(){
        return ResponseEntity.ok(commentService.getAll());
    }

    @GetMapping(path ="users/{seller_id}/comments/{comment_id}")
    public ResponseEntity<CommentResponseDTO> getComment(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id){
        return ResponseEntity.ok(commentService.getComment(seller_id,comment_id,true));
    }

    @PostMapping(path ="users/{seller_id}/comments/{comment_id}")
    public ResponseEntity<CommentResponseDTO> approveComment(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id,
            @RequestParam("confirm") Boolean confirm){
        return ResponseEntity.status(202).body(commentService.approveComment(seller_id,comment_id,confirm));
    }

    @GetMapping(path = "users/inactive")
    public ResponseEntity<List<UserDTO>> getInactiveUsers(){
        return ResponseEntity.ok(userService.getInactiveUsers());
    }
}
