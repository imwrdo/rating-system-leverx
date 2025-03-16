package org.leverx.ratingapp.services.comment;

import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;

import java.util.List;
/**
 * CommentService is interface for managing comments related to sellers.
 * Handles the creation, retrieval, update, approval, and deletion of comments.
 */
public interface CommentService {
     // Creates a new comment for a seller
     CommentResponseDTO create(Long sellerId, CommentRequestDTO commentObject);

     // Retrieves all comments for a specific seller.
     List<CommentResponseDTO> getAllBySellerId(Long sellerId, Boolean isAdmin);

     // Retrieves a specific comment by its ID for a seller
     CommentResponseDTO getComment(Long sellerId, Long commentId, Boolean isAdmin);

     // Deletes a comment and updates the seller's rating accordingly.
     String delete(Long sellerId, Long commentId);

     // Updates an existing comment's message and grade
     CommentResponseDTO update(Long sellerId, Long commentId, CommentRequestDTO commentObject);

     // Approves or deletes a comment based on the given confirmation.
     CommentResponseDTO approveComment(Long sellerId, Long commentId,Boolean confirm);

     // Retrieves all comments from the system
     List<CommentResponseDTO> getAll();
}
