package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.auth.registration.RegistrationRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.dtos.comments.CommentWithRegistrationRequestDTO;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationService;
import org.leverx.ratingapp.services.comment.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path ="users/{seller_id}/comments")
public class CommentController {

    private final CommentService commentService;
    private final AuthenticationAndRegistrationService authAndRegService;
    private final UserRepository userRepository;
    @PostMapping
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable Long seller_id,
            @RequestBody CommentRequestDTO commentObject){

        CommentResponseDTO response = commentService.create(seller_id, commentObject);
        return ResponseEntity.created(
                URI.create(String.format("/users/%d/comments/%d", seller_id, response.id()))
        ).body(response);
    }

    @PostMapping(path="optional-seller")
    public ResponseEntity<CommentResponseDTO> createCommentWithOptionalSeller(
            @PathVariable Long seller_id,
            @RequestBody CommentWithRegistrationRequestDTO request) {

        boolean sellerExists = userRepository.existsById(seller_id);

        if (!sellerExists) {
            RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO(
                    request.firstName(),
                    request.lastName(),
                    request.password(),
                    request.email()
            );
            authAndRegService.register(registrationRequest);
        }

        CommentResponseDTO response = commentService.create(seller_id, new CommentRequestDTO(request.message()));

        return ResponseEntity.created(
                URI.create(String.format("/users/%d/comments/%d", seller_id, response.id()))
        ).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getAllAcceptedComments(
            @PathVariable Long seller_id){

        return ResponseEntity.ok(commentService.getAllBySellerId(seller_id,false));
    }

    @GetMapping(path ="{comment_id}")
    public ResponseEntity<CommentResponseDTO> getComment(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id){
        return ResponseEntity.ok(commentService.getComment(seller_id,comment_id,false));
    }

    @DeleteMapping(path = "{comment_id}")
    public ResponseEntity<String> delete(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id){

        return ResponseEntity.status(202).body(commentService.delete(seller_id,comment_id));
    }


    @PutMapping(path = "{comment_id}")
    public ResponseEntity<CommentResponseDTO> update(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id,
            @RequestBody CommentRequestDTO commentObject){

        return ResponseEntity.ok(commentService.update(seller_id,comment_id,commentObject));
    }



}
