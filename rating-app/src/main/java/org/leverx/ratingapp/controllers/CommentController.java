package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;

import org.leverx.ratingapp.dtos.CommentObjectDTO;
import org.leverx.ratingapp.dtos.CommentResponseDTO;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.services.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path ="users")
public class CommentController {

    private final CommentService commentService;

    @PostMapping(path ="{seller_id}/comments")
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable Long seller_id,
            @RequestBody CommentObjectDTO commentObject){
        Comment comment = commentService.create(seller_id,commentObject);
        return ResponseEntity.ok(
                CommentResponseDTO.builder()
                        .message(comment.getMessage())
                        .author(comment.getAuthor().getEmail())
                        .seller(comment.getSeller().getEmail())
                        .Status("Comment is created, please wait for verification")
                .build());
    }

}
