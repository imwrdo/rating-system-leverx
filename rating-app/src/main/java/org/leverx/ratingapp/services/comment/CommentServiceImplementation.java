package org.leverx.ratingapp.services.comment;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.models.entities.Comment;
import org.leverx.ratingapp.models.entities.User;
import org.leverx.ratingapp.models.enums.Status;
import org.leverx.ratingapp.exceptions.ResourceNotFoundException;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.auth.authorization.AuthorizationServiceImplementation;
import org.leverx.ratingapp.services.rating.RatingCalculationServiceImplementation;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service implementation of {@link CommentService} for managing comments related to sellers.
 * Handles the creation, retrieval, update, approval, and deletion of comments.
 */
@Service
@AllArgsConstructor
public class CommentServiceImplementation implements CommentService {
    // Repositories for accessing comment and user data
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // Services for authorization and rating calculation
    private final AuthorizationServiceImplementation authorizationService;
    private final RatingCalculationServiceImplementation ratingCalculationServiceImplementation;

    /**
     * Creates a new comment for a seller.
     * Ensures the grade is between 1 and 5. The comment is saved and seller's rating is updated if approved.
     *
     * @param sellerId The ID of the seller being reviewed.
     * @param commentObject The comment details from the client.
     * @return The created comment wrapped in a {@link CommentResponseDTO}.
     */
    @Transactional
    @Override
    public CommentResponseDTO create(Long sellerId, CommentRequestDTO commentObject) {
        User currentUser = authorizationService.getCurrentUser();
        User seller = userRepository.findActiveUserById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Seller with id %d not found",
                        sellerId)));

        // Validate the grade range (1 to 5)
        if (commentObject.grade() < 1 || commentObject.grade() > 5) {
            throw new IllegalArgumentException("Grade must be between 1 and 5");
        }

        // Build and save the comment
        var comment = Comment.builder()
                .message(commentObject.message())
                .grade(commentObject.grade())
                .author(currentUser)
                .seller(seller)
                .build();

        commentRepository.save(comment);

        // Update seller's rating if the comment is approved
        if (comment.getIsApproved()) {
            ratingCalculationServiceImplementation.updateSellerRating(sellerId);
        }

        // Return the comment wrapped in a DTO
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

    /**
     * Retrieves all comments for a specific seller.
     * Admins can view all comments; authenticated users can view their own comments and accepted ones;
     * Anonymous users can only view accepted comments.
     *
     * @param sellerId The ID of the seller.
     * @param isAdmin Boolean indicating if the request is made by an admin.
     * @return A list of comment {@link CommentResponseDTO} for the seller.
     */
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

    /**
     * Retrieves a specific comment by its ID for a seller.
     * Admins can view any comment; authenticated users can view their own approved or pending comments.
     *
     * @param sellerId The ID of the seller.
     * @param commentId The ID of the comment.
     * @param isAdmin Boolean indicating if the request is made by an admin.
     * @return A {@link CommentResponseDTO} containing the comment details.
     */
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

    /**
     * Deletes a comment and updates the seller's rating accordingly.
     * Only authorized users (e.g., comment author or admin) can delete comments.
     *
     * @param sellerId The ID of the seller.
     * @param commentId The ID of the comment.
     * @return A status message indicating the deletion result.
     */
    @Transactional
    @Override
    public String delete(Long sellerId, Long commentId) {
        User currentUser = authorizationService.getRequiredCurrentUser();
        Comment comment = getRequiredComment(sellerId, commentId);

        // Authorize modification before deleting the comment
        authorizationService.authorizeResourceModification(comment, currentUser);
        commentRepository.delete(comment);
        ratingCalculationServiceImplementation.updateSellerRating(sellerId);

        return String.format("Comment %s is %s",
                commentId,
                Status.DELETED.getValueOfStatus());
    }

    /**
     * Updates an existing comment's message and grade.
     * The comment can only be modified by its author or an admin.
     *
     * @param sellerId The ID of the seller.
     * @param commentId The ID of the comment to update.
     * @param commentObject The updated comment details.
     * @return The updated comment wrapped in a response DTO.
     */
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

    /**
     * Approves or deletes a comment based on the given confirmation.
     * The seller can approve or delete comments, and the seller's rating will be updated accordingly.
     *
     * @param sellerId The ID of the seller.
     * @param commentId The ID of the comment.
     * @param confirm Boolean flag to confirm approval or deletion.
     * @return A {@link CommentResponseDTO} containing the status of the comment.
     */
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

    /**
     * Retrieves all comments from the system.
     *
     * @return A list of all comments wrapped in {@link CommentResponseDTO}.
     */
    @Override
    public List<CommentResponseDTO> getAll() {
        List<Comment> comments = commentRepository.findAll();
        return CommentResponseDTO.mapToCommentResponseDTO(comments);
    }

    /**
     * Helping function, which retrieves the required comment by sellerId and commentId.
     *
     * @param sellerId The ID of the seller.
     * @param commentId The ID of the comment.
     * @return The requested {@link Comment}.
     */
    private Comment getRequiredComment(Long sellerId, Long commentId) {
        return commentRepository.findByIdAndSellerId(commentId, sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Comment for seller %d and id %d not found", sellerId, commentId)));
    }
}
