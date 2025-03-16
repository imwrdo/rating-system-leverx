package org.leverx.ratingapp.services.comment;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.enums.Status;
import org.leverx.ratingapp.exceptions.ResourceNotFoundException;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.auth.AuthorizationService;
import org.leverx.ratingapp.services.rating.RatingCalculationServiceImplementation;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Stream;


@Service
@AllArgsConstructor
public class CommentServiceImplementation implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;
    private final RatingCalculationServiceImplementation ratingCalculationServiceImplementation;

    @Transactional
    @Override
    public CommentResponseDTO create(Long sellerId, CommentRequestDTO commentObject) {
        User currentUser = authorizationService.getCurrentUser();
        User seller = userRepository.findActiveUserById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Seller with id %d not found",
                        sellerId)));

        if (commentObject.grade() < 1 || commentObject.grade() > 5) {
            throw new IllegalArgumentException("Grade must be between 1 and 5");
        }

        var comment = Comment.builder()
                .message(commentObject.message())
                .grade(commentObject.grade())
                .author(currentUser)
                .seller(seller)
                .build();

        commentRepository.save(comment);
        if (comment.getIsApproved()) {
            ratingCalculationServiceImplementation.updateSellerRating(sellerId);
        }
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .grade(comment.getGrade())
                .author(currentUser != null ? currentUser.getEmail() : null)
                .seller(seller.getEmail())
                .status(String.format("Comment is %s, please wait for verification",
                        Status.CREATED.getValueOfStatus()))
                .build();
    }

    @Override
    public List<CommentResponseDTO> getAllBySellerId(Long sellerId, Boolean isAdmin) {
        User currentUser = authorizationService.getCurrentUser();

        boolean isSellerExists = isAdmin
                ? userRepository
                    .existsById(sellerId)
                : userRepository
                    .existsActiveUserById(sellerId);

        if (!isSellerExists) {
            throw new ResourceNotFoundException(String.format("Seller with id %d not found",
                    sellerId));
        }

        List<Comment> comments;

        if (isAdmin) {
            // Admins can view all comments regardless of status or author
            comments = commentRepository.findAllBySellerId(sellerId);
        } else {
            if (currentUser != null) {
                String currentUserEmail = currentUser.getEmail();
                // Authenticated user can see both accepted comments and their own
                comments = Stream.concat(
                                commentRepository.findAllAcceptedBySellerId(sellerId).stream(),
                                commentRepository.findAllBySellerId(sellerId).stream()
                                        .filter(comment -> comment.getAuthor().getEmail().equals(currentUserEmail))
                        )
                        .distinct()
                        .toList();
            } else {
                // Anonymous users can only see accepted comments
                comments = commentRepository.findAllAcceptedBySellerId(sellerId);
            }
        }


        return CommentResponseDTO.mapToCommentResponseDTO(comments);
    }

    @Override
    public CommentResponseDTO getComment(Long sellerId, Long commentId, Boolean isAdmin) {
        User currentUser = authorizationService.getCurrentUser();

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
                .status(comment.getIsApproved()
                        ? Status.APPROVED.getValueOfStatus()
                        : Status.PENDING.getValueOfStatus()
                )
                .build();
    }

    @Transactional
    @Override
    public String delete(Long sellerId, Long commentId) {
        User currentUser = authorizationService.getRequiredCurrentUser();
        Comment comment = getRequiredComment(sellerId, commentId);
        
        authorizationService.authorizeResourceModification(comment, currentUser);
        commentRepository.delete(comment);
        ratingCalculationServiceImplementation.updateSellerRating(sellerId);

        return String.format("Comment %s is %s",
                commentId,
                Status.DELETED.getValueOfStatus());
    }

    @Transactional
    @Override
    public CommentResponseDTO update(Long sellerId, Long commentId, CommentRequestDTO commentObject) {

        User currentUser = authorizationService.getCurrentUser();
        userRepository.findById(sellerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("Seller with id %d not found", sellerId)));

        commentRepository.findById(commentId)
                .orElseThrow(()->
                        new ResourceNotFoundException(String.format("Comment with id %d not found", commentId)));

        var comment =  commentRepository.findByIdAndSellerId(commentId,sellerId)
                .map(existingComment -> {
                    authorizationService.authorizeResourceModification(existingComment,currentUser);
                    existingComment.setMessage(commentObject.message());
                    existingComment.setGrade(commentObject.grade());
                    commentRepository.save(existingComment);
                    return existingComment;
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Comment for seller %d and id %d not found",sellerId,commentId)));
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .author(comment.getAuthor().getEmail())
                .seller(comment.getSeller().getEmail())
                .grade(comment.getGrade())
                .status(Status.UPDATED.getValueOfStatus())
                .build();
    }

    @Transactional
    @Override
    public CommentResponseDTO approveComment(Long sellerId, Long commentId,Boolean confirm) {
        // Ensure seller exists
        if (!userRepository.existsById(sellerId)) {
            throw new ResourceNotFoundException(String.format("Seller with id %d not found", sellerId));
        }

        // Fetch and handle the comment
        Comment comment = commentRepository.findByIdAndSellerId(commentId, sellerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("Comment for seller %d and id %d not found", sellerId, commentId)));

        if (confirm) {
            comment.setIsApproved(true);
            commentRepository.save(comment);
            ratingCalculationServiceImplementation.updateSellerRating(sellerId);
        } else {
            commentRepository.delete(comment);
            ratingCalculationServiceImplementation.updateSellerRating(sellerId);
        }

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .author(comment.getAuthor().getEmail())
                .seller(comment.getSeller().getEmail())
                .status(confirm
                        ? Status.APPROVED.getValueOfStatus()
                        : Status.DELETED.getValueOfStatus()
                )
                .grade(comment.getGrade())
                .build();
    }

    @Override
    public List<CommentResponseDTO> getAll() {
        List<Comment> comments = commentRepository.findAll();
        return CommentResponseDTO.mapToCommentResponseDTO(comments);
    }

    private Comment getRequiredComment(Long sellerId, Long commentId) {
        return commentRepository.findByIdAndSellerId(commentId, sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Comment for seller %d and id %d not found", sellerId, commentId)));
    }
}
