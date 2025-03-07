package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.services.comment.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path ="users")
public class CommentController {

    private final CommentService service;

    @PostMapping(path ="{seller_id}/comments")
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable Long seller_id,
            @RequestBody CommentRequestDTO commentObject){

        return ResponseEntity.ok(service.create(seller_id,commentObject));
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

        return ResponseEntity.ok(service.delete(seller_id,comment_id));
    }


    @PutMapping(path = "{seller_id}/comments/{comment_id}")
    public ResponseEntity<CommentResponseDTO> update(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id,
            @RequestBody CommentRequestDTO commentObject){

        return ResponseEntity.ok(service.update(seller_id,comment_id,commentObject));
    }

}
