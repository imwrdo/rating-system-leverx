package org.leverx.ratingapp.services.comment;

import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;

import java.util.List;

public interface CommentService {
     CommentResponseDTO create(Long sellerId, CommentRequestDTO commentObject);
     List<CommentResponseDTO> getAllBySellerId(Long sellerId, Boolean isAdmin);
     CommentResponseDTO getComment(Long sellerId, Long commentId, Boolean isAdmin);
     String delete(Long sellerId, Long commentId);
     CommentResponseDTO update(Long sellerId, Long commentId, CommentRequestDTO commentObject);
     CommentResponseDTO approveComment(Long sellerId, Long commentId,Boolean confirm);
     List<CommentResponseDTO> getAll();
}
