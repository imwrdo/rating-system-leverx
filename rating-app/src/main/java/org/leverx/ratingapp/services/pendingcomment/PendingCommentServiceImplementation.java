package org.leverx.ratingapp.services.pendingcomment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.PendingCommentDTO;
import org.leverx.ratingapp.exceptions.InvalidOperationException;
import org.leverx.ratingapp.repositories.redis.PendingCommentRedisRepository;
import org.leverx.ratingapp.services.comment.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of {@link PendingCommentService} for handling pending comments.
 * This service manages saving pending comments to a repository and processing them when appropriate.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PendingCommentServiceImplementation implements PendingCommentService {
    private final PendingCommentRedisRepository pendingCommentRedisRepository;
    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    /**
     * Saves a pending comment to the repository for a specific seller and user.
     * The comment is stored as a JSON string in the repository.
     *
     * @param email The email of the user who is leaving the comment.
     * @param sellerId The ID of the seller being commented on.
     * @param comment The message content of the comment.
     * @param grade The grade/score associated with the comment.
     * @throws InvalidOperationException If there is an issue with serializing or saving the pending comment.
     */
    @Override
    public void savePendingComment(String email, Long sellerId, String comment,Integer grade) {
        // Create a PendingCommentDTO with the provided details
        PendingCommentDTO pendingComment = PendingCommentDTO.builder()
                .sellerId(sellerId)
                .message(comment)
                .grade(grade)
                .build();
        try {
            // Convert the pending comment DTO to a JSON string
            String commentJson = objectMapper.writeValueAsString(pendingComment);
            pendingCommentRedisRepository.save(email, commentJson);
        } catch (JsonProcessingException e) {
            // Handle the case where JSON serialization fails
            throw new InvalidOperationException("Failed to save pending comment");
        }
    }

    /**
     * Processes the pending comment for a specific user.
     * If there is a pending comment, it is retrieved, converted to a DTO, and processed by creating a real comment.
     * After successful processing, the pending comment is removed from the repository.
     *
     * @param email The email of the user whose pending comment should be processed.
     * @throws InvalidOperationException If there is an issue with deserializing or processing the pending comment.
     */
    @Override
    public void processPendingComment(String email) {
        // Retrieve the pending comment JSON string from the repository using the user's email
        String pendingCommentJson = pendingCommentRedisRepository.get(email);

        // If a pending comment exists for the email, process it
        if (pendingCommentJson != null) {
            try {
                // Deserialize the JSON string into a PendingCommentDTO object
                PendingCommentDTO pendingComment = objectMapper.readValue(pendingCommentJson, PendingCommentDTO.class);

                // Create a new CommentRequestDTO and process the comment through the comment service
                commentService.create(pendingComment.sellerId(),
                        new CommentRequestDTO(pendingComment.message(),pendingComment.grade()));

                // Remove the processed pending comment from the repository
                pendingCommentRedisRepository.remove(email);
            } catch (JsonProcessingException e) {
                // Handle the case where JSON deserialization fails
                throw new InvalidOperationException("Failed to process pending comment");
            }
        }
    }
}
