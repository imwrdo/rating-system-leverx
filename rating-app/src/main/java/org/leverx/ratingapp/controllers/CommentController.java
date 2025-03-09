package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.UserDTO;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.services.comment.CommentService;
import org.leverx.ratingapp.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path ="users")
public class CommentController {

    private final CommentService service;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllActivatedUsers() {
        List<UserDTO> users = userService.getAllActivatedUsers();
        return ResponseEntity.ok(users);
    }


    @PostMapping(path ="{seller_id}/comments")
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable Long seller_id,
            @RequestBody CommentRequestDTO commentObject){

        CommentResponseDTO response = service.create(seller_id, commentObject);
        return ResponseEntity.created(
                URI.create(String.format("/users/%d/comments/%d", seller_id, response.id()))
        ).body(response);
    }

    @GetMapping(path ="{seller_id}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getAll(
            @PathVariable Long seller_id){

        return ResponseEntity.ok(service.getAll(seller_id));
    }

    @GetMapping(path ="{seller_id}/comments/{comment_id}")
    public ResponseEntity<CommentResponseDTO> getComment(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id){
        return ResponseEntity.ok(service.getComment(seller_id,comment_id));
    }

    @DeleteMapping(path = "{seller_id}/comments/{comment_id}")
    public ResponseEntity<String> delete(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id){

        return ResponseEntity.status(202).body(service.delete(seller_id,comment_id));
    }


    @PutMapping(path = "{seller_id}/comments/{comment_id}")
    public ResponseEntity<CommentResponseDTO> update(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id,
            @RequestBody CommentRequestDTO commentObject){

        return ResponseEntity.ok(service.update(seller_id,comment_id,commentObject));
    }

}
