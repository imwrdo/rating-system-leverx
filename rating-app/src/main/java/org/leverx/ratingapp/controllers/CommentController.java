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

/**
 * CommentController is a REST controller that manages comment-related actions
 * for a seller's profile. It allows creating, retrieving, updating, and deleting
 * comments, as well as handling registration for users without existing accounts.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path ="users/{seller_id}/comments")
public class CommentController {
    private final CommentService commentService; // Service for handling comment operations
    private final AuthenticationAndRegistrationService authAndRegService; // Service for authentication and registration
    private final UserRepository userRepository; // Repository for managing users

    /**
     * Endpoint to create a new comment for a specific seller.
     *
     * @param seller_id the seller's ID
     * @param commentObject the details of the comment to create
     * @return a ResponseEntity containing the created comment as a CommentResponseDTO object
     */
    @PostMapping
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable Long seller_id,
            @RequestBody CommentRequestDTO commentObject){

        CommentResponseDTO response = commentService.create(seller_id, commentObject);
        return ResponseEntity.created(
                URI.create(String.format("/users/%d/comments/%d", seller_id, response.id()))
        ).body(response);
    }

    /**
     * Endpoint to create a comment with an optional seller. If the seller does not exist,
     * a user is registered first before the comment is created.
     *
     * @param seller_id the seller's ID
     * @param request the request object containing comment details and user registration information
     * @return a {@link ResponseEntity} containing the created comment or a temporary response
     * if the user is pending registration
     */
    @PostMapping(path="optional-seller")
    public ResponseEntity<CommentResponseDTO> createCommentWithOptionalSeller(
            @PathVariable Long seller_id,
            @RequestBody CommentWithRegistrationRequestDTO request) {

        boolean sellerExists = userRepository.existsById(seller_id);
        CommentResponseDTO response;

        // If seller doesn't exist, register the user first
        if (!sellerExists) {
            RegistrationRequestDTO registrationRequest = new RegistrationRequestDTO(
                    request.firstName(),
                    request.lastName(),
                    request.password(),
                    request.email()
            );
            // Register user first
            authAndRegService.registerWithPendingComment(
                    registrationRequest,
                    seller_id,
                    request.message(),
                    request.grade());
            
            // Return temporary response
            response = CommentResponseDTO.builder()
                    .message(request.message())
                    .status("Comment pending - awaiting account activation")
                    .build();
        } else {
            response = commentService.create(seller_id, new CommentRequestDTO(request.message(),request.grade()));
        }

        return ResponseEntity.created(
                URI.create(String.format("/users/%d/comments/%d", seller_id, response.id()))
        ).body(response);
    }

    /**
     * Endpoint to retrieve all accepted comments for a specific seller.
     *
     * @param seller_id the seller's ID
     * @return a ResponseEntity containing a list of all accepted comments for the seller as CommentResponseDTO objects
     */
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getAllAcceptedComments(
            @PathVariable Long seller_id){

        return ResponseEntity.ok(commentService.getAllBySellerId(seller_id,false));
    }

    /**
     * Endpoint to retrieve a specific comment by its ID for a specific seller.
     *
     * @param seller_id the seller's ID
     * @param comment_id the ID of the comment to retrieve
     * @return a ResponseEntity containing the requested comment as a CommentResponseDTO object
     */
    @GetMapping(path ="{comment_id}")
    public ResponseEntity<CommentResponseDTO> getComment(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id){
        return ResponseEntity.ok(commentService.getComment(seller_id,comment_id,false));
    }

    /**
     * Endpoint to delete a comment by its ID for a specific seller.
     *
     * @param seller_id the seller's ID
     * @param comment_id the ID of the comment to delete
     * @return a ResponseEntity containing a status message after deletion
     */
    @DeleteMapping(path = "{comment_id}")
    public ResponseEntity<String> delete(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id){

        return ResponseEntity.status(202).body(commentService.delete(seller_id,comment_id));
    }

    /**
     * Endpoint to update an existing comment by its ID for a specific seller.
     *
     * @param seller_id the seller's ID
     * @param comment_id the ID of the comment to update
     * @param commentObject the updated comment details
     * @return a ResponseEntity containing the updated comment as a CommentResponseDTO object
     */
    @PutMapping(path = "{comment_id}")
    public ResponseEntity<CommentResponseDTO> update(
            @PathVariable Long seller_id,
            @PathVariable Long comment_id,
            @RequestBody CommentRequestDTO commentObject){

        return ResponseEntity.ok(commentService.update(seller_id,comment_id,commentObject));
    }



}
