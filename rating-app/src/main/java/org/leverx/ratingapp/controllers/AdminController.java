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
    public ResponseEntity<String> confirm(@RequestParam("token") String token){

        return ResponseEntity.ok(authAndRegService.confirmToken(token));
    }

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

    @GetMapping(path ="{seller_id}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getAllAcceptedComments(
            @PathVariable Long seller_id){
        return ResponseEntity.ok(commentService.getAll(seller_id,true));
    }

    @GetMapping(path ="{seller_id}/comments/{comment_id}")
    public ResponseEntity<CommentResponseDTO> getComment(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id){
        return ResponseEntity.ok(commentService.getComment(seller_id,comment_id,true));
    }

    @PostMapping(path ="{seller_id}/comments/{comment_id}")
    public ResponseEntity<CommentResponseDTO> approveComment(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id){
        return ResponseEntity.ok(commentService.approveComment(seller_id,comment_id));
    }
}
