package org.leverx.ratingapp.services.comment;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.exceptions.ResourceNotFoundException;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationServiceImplementation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;


@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImplementation implements CommentService {
    private CommentRepository commentRepository;
    private AuthenticationAndRegistrationServiceImplementation authAndRegService;
    private UserRepository userRepository;

    @Override
    public CommentResponseDTO create(Long sellerId, CommentRequestDTO commentObject) {

        User currentUser = authAndRegService.getCurrentUser();

        User seller = userRepository.findActiveUserById(sellerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("Seller with id %d not found",sellerId)));

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

    @Override
    public List<CommentResponseDTO> getAllBySellerId(Long sellerId, Boolean isAdmin) {

        User currentUser = authAndRegService.getCurrentUser();

        boolean isSellerExists = isAdmin
                ? userRepository.existsById(sellerId)
                : userRepository.existsActiveUserById(sellerId);

        if (!isSellerExists) {
            throw new ResourceNotFoundException(String.format("Seller with id %d not found", sellerId));
        }

        List<Comment> comments = isAdmin
                ? commentRepository.findAllBySellerId(sellerId)
                : Stream.concat(
                        commentRepository.findAllAcceptedBySellerId(sellerId).stream(),
                        commentRepository.findAllBySellerId(sellerId).stream()
                                .filter(comment -> comment.getAuthor().getEmail().equals(currentUser.getEmail()))
                )
                .distinct()
                .toList();

        return CommentResponseDTO.mapToCommentResponseDTO(comments);
    }

    @Override
    public CommentResponseDTO getComment(Long sellerId, Long commentId, Boolean isAdmin) {
        User currentUser = authAndRegService.getCurrentUser();

        Comment comment = commentRepository.findByIdAndSellerId(commentId, sellerId)
                .filter(c -> isAdmin
                        || c.getIsApproved()
                        || c.getAuthor().getEmail().equals(currentUser.getEmail()))
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("Comment for seller %d and id %d not found", sellerId, commentId)));
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .author(comment.getAuthor().getEmail())
                .seller(comment.getSeller().getEmail())
                .status(comment.getIsApproved()?"Approved":"Pending")
                .build();
    }

    @Override
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


    @Override
    public CommentResponseDTO update(Long sellerId, Long commentId, CommentRequestDTO commentObject) {

        User currentUser = authAndRegService.getCurrentUser();
        userRepository.findById(sellerId)
                .orElseThrow(() ->
                        new RuntimeException(String.format("Seller with id %d not found", sellerId)));

        commentRepository.findById(commentId)
                .orElseThrow(()->
                        new RuntimeException(String.format("Comment with id %d not found", commentId)));

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

    @Override
    public CommentResponseDTO approveComment(Long sellerId, Long commentId,Boolean confirm) {
        // Ensure seller exists
        if (!userRepository.existsById(sellerId)) {
            throw new RuntimeException(String.format("Seller with id %d not found", sellerId));
        }

        // Fetch and handle the comment
        Comment comment = commentRepository.findByIdAndSellerId(commentId, sellerId)
                .orElseThrow(() ->
                        new RuntimeException(String.format("Comment for seller %d and id %d not found", sellerId, commentId)));

        if (confirm) {
            comment.setIsApproved(true);
            commentRepository.save(comment);
        } else {
            commentRepository.delete(comment);
        }

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .author(comment.getAuthor().getEmail())
                .seller(comment.getSeller().getEmail())
                .status(confirm ? "Approved" : "Deleted")
                .build();
    }

    @Override
    public List<CommentResponseDTO> getAll() {
        List<Comment> comments = commentRepository.findAll();
        return CommentResponseDTO.mapToCommentResponseDTO(comments);
    }


}
