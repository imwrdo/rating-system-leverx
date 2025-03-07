package org.leverx.ratingapp.services;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class CommentService {
    private CommentRepository commentRepository;
    private AuthenticationAndRegistrationService authAndRegService;
    private UserRepository userRepository;

    public CommentResponseDTO create(Long sellerId, CommentRequestDTO commentObject) {
        User currentUser = authAndRegService.getCurrentUser();

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() ->
                        new RuntimeException(String.format("Seller with id %d not found",sellerId)));

        var comment = Comment.builder()
                .message(commentObject.message())
                .author(currentUser)
                .seller(seller)
                .build();
        commentRepository.save(comment);
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .author(comment.getAuthor().getEmail())
                .seller(comment.getSeller().getEmail())
                .status("Comment is created, please wait for verification")
                .build();
    }

    public List<CommentResponseDTO> getAll(Long sellerId) {

        userRepository.findById(sellerId)
                .orElseThrow(() ->
                        new RuntimeException(String.format("Seller with id %d not found",sellerId)));

        List<Comment> comments = commentRepository.findAllBySellerId(sellerId);

        return CommentResponseDTO.mapToCommentResponseDTO(comments);
    }

    public CommentResponseDTO getComment(Long sellerId, Long commentId) {

        var comment =  commentRepository.findByIdAndSellerId(commentId,sellerId)
                .orElseThrow(() -> new RuntimeException(String.format("Comment for seller %d and id %d not found",sellerId,commentId)));

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .author(comment.getAuthor().getEmail())
                .seller(comment.getSeller().getEmail())
                .status(comment.getIs_approved()?"Approved":"Pending")
                .build();
    }

    public String delete(Long sellerId, Long commentId) {
        userRepository.findById(sellerId)
                .orElseThrow(() ->
                        new RuntimeException(String.format("Seller with id %d not found",sellerId)));
        commentRepository.findById(commentId)
                .orElseThrow(()->
                        new RuntimeException(String.format("Comment with id %d not found",commentId)));

        User currentUser = authAndRegService.getCurrentUser();

        Comment existingComment = commentRepository.findByIdAndSellerId(commentId,sellerId)
                .orElseThrow(() ->
                        new RuntimeException(String.format("Comment for seller %d and id %d not found",sellerId,commentId)));

        authAndRegService.authorizeUser(existingComment,currentUser);
        commentRepository.delete(existingComment);

        return "Your comment is deleted successfully";
    }

    public CommentResponseDTO update(Long sellerId, Long commentId, CommentRequestDTO commentObject) {

        User currentUser = authAndRegService.getCurrentUser();
        userRepository.findById(sellerId)
                .orElseThrow(() ->
                        new RuntimeException(String.format("Seller with id %d not found",sellerId)));

        commentRepository.findById(commentId)
                .orElseThrow(()->
                        new RuntimeException(String.format("Comment with id %d not found",commentId)));

        var comment =  commentRepository.findByIdAndSellerId(commentId,sellerId)
                .map(existingComment -> {
                    authAndRegService.authorizeUser(existingComment,currentUser);
                    existingComment.setMessage(commentObject.message());
                    commentRepository.save(existingComment);
                    return existingComment;
                })
                .orElseThrow(() -> new RuntimeException(String.format("Comment for seller %d and id %d not found",sellerId,commentId)));
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .author(comment.getAuthor().getEmail())
                .seller(comment.getSeller().getEmail())
                .status("Modified")
                .build();
    }


}
