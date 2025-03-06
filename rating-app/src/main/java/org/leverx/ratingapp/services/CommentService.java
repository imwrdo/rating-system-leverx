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
                .message(commentObject.getMessage())
                .author(currentUser)
                .seller(seller)
                .build();
        commentRepository.save(comment);
        return CommentResponseDTO.builder()
                .message(comment.getMessage())
                .author(comment.getAuthor().getEmail())
                .seller(comment.getSeller().getEmail())
                .Status("Comment is created, please wait for verification")
                .build();
    }

    public List<CommentResponseDTO> getAll(Long sellerId) {

        userRepository.findById(sellerId)
                .orElseThrow(() ->
                        new RuntimeException(String.format("Seller with id %d not found",sellerId)));
        List<Comment> comments = commentRepository.findAllBySellerId(sellerId);

        return CommentResponseDTO.mapToCommentResponseDTO(comments);


    }
}
