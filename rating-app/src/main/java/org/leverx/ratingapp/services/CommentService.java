package org.leverx.ratingapp.services;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.CommentObjectDTO;
import org.leverx.ratingapp.dtos.CommentResponseDTO;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {
    private CommentRepository commentRepository;
    private AuthenticationAndRegistrationService authAndRegService;
    private UserRepository userRepository;

    public Comment create(Long sellerId, CommentObjectDTO commentObject) {
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
        return comment;
    }
}
