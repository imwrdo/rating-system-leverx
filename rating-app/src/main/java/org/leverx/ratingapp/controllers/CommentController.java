package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;

import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.services.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path ="users")
public class CommentController {

    private final CommentService commentService;

    @PostMapping(path ="{seller_id}/comments")
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable Long seller_id,
            @RequestBody CommentRequestDTO commentObject){
        return ResponseEntity.ok(commentService.create(seller_id,commentObject));
    }

    @GetMapping(path ="{seller_id}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getAll(
            @PathVariable Long seller_id){
        return ResponseEntity.ok(commentService.getAll(seller_id));
    }



}
