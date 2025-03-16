package org.leverx.ratingapp.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.leverx.ratingapp.dtos.comments.CommentRequestDTO;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.models.entities.Comment;
import org.leverx.ratingapp.models.entities.User;
import org.leverx.ratingapp.models.enums.Status;
import org.leverx.ratingapp.exceptions.ForbiddenException;
import org.leverx.ratingapp.exceptions.ResourceNotFoundException;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.auth.authorization.AuthorizationServiceImplementation;
import org.leverx.ratingapp.services.comment.CommentServiceImplementation;
import org.leverx.ratingapp.services.rating.RatingCalculationServiceImplementation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link CommentServiceImplementation class} .
 * This class tests various functionalities of the comment service,
 * including creating, approving, updating, retrieving, and deleting comments.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Comment Service Unit Tests")
class CommentServiceUnitTests {

    @Mock private CommentRepository commentRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuthorizationServiceImplementation authorizationService;
    @Mock private RatingCalculationServiceImplementation ratingCalculationServiceImplementation;


    @InjectMocks
    private CommentServiceImplementation commentService;

    private User seller;
    private User author;

    /**
     * Sets up test data before each test case.
     */
    @BeforeEach
    void setUp() {

        seller = User.builder()
                .id(1L)
                .email("seller@test.com")
                .build();

        author = User.builder()
                .id(2L)
                .email("author@test.com")
                .build();
    }

    /**
     * Test for successful comment creation.
     * Arrange: Create comment request with message and grade
     *         Mock user repository to return seller
     *         Mock authorization service to return author
     *         Mock comment repository save operation
     * Act: Call create with seller ID and comment request
     * Assert: Verify response matches input data
     *         Verify comment was saved
     */
    @Test
    @DisplayName("Create comment successfully")
    void testCreateComment() {
        // Arrange
        CommentRequestDTO request = new CommentRequestDTO("Great seller!", 5);
        when(userRepository.findActiveUserById(seller.getId()))
                .thenReturn(Optional.of(seller));
        when(authorizationService.getCurrentUser())
                .thenReturn(author);
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        // Act
        CommentResponseDTO response = commentService.create(seller.getId(), request);

        // Assert
        assertNotNull(response);
        assertEquals("Great seller!", response.message());
        assertEquals(5, response.grade());
        verify(commentRepository).save(any(Comment.class));
    }

    /**
     * Test for comment creation with invalid seller.
     * Arrange: Create comment request
     *         Mock user repository to return empty result
     * Act & Assert: Verify ResourceNotFoundException is thrown
     *              Verify no comment was saved
     */
    @Test
    @DisplayName("Create comment with invalid seller ID throws exception")
    void testCreateCommentInvalidSeller() {
        // Arrange
        CommentRequestDTO request = new CommentRequestDTO("Great seller!", 5);
        when(userRepository.findActiveUserById(any()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.create(999L, request));
        verify(commentRepository, never()).save(any());
    }

    /**
     * Test of successful approval of a comment.
     * Arrange: Create comment with isApproved set to false
     *         Mock user repository to return true for seller existence
     *         Mock comment repository to return comment by ID and seller ID
     *         Mock comment repository save operation
     * Act: Call approveComment with seller ID, comment ID, and approval status
     * Assert: Verify response is not null
     *         Verify comment was saved with isApproved set to true
     *         Verify seller rating was updated
     */
    @Test
    @DisplayName("Approve comment successfully")
    void testApproveComment() {
        // Arrange
        Comment comment = Comment.builder()
                .id(1L)
                .message("Great seller!")
                .grade(5)
                .seller(seller)
                .author(author)
                .isApproved(false)
                .build();

        when(userRepository.existsById(seller.getId())).thenReturn(true);
        when(commentRepository.findByIdAndSellerId(1L, seller.getId()))
                .thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        // Act
        CommentResponseDTO response = commentService.approveComment(seller.getId(), 1L, true);

        // Assert
        assertNotNull(response);
        verify(commentRepository).save(argThat(c -> ((Comment) c).getIsApproved()));
        verify(ratingCalculationServiceImplementation).updateSellerRating(seller.getId());
    }

    /**
     * Test that creates a comment with an invalid grade throws an exception.
     * Arrange: Create comment request with invalid grade
     *         Mock user repository to return seller
     * Act & Assert: Verify IllegalArgumentException is thrown
     */
    @Test
    @DisplayName("Create comment with invalid grade throws exception")
    void testCreateCommentInvalidGrade() {
        //Arrange
        CommentRequestDTO invalidRequest = new CommentRequestDTO("Great seller!", 6);
        when(userRepository.findActiveUserById(seller.getId()))
                .thenReturn(Optional.of(seller));
        // Act & create
        assertThrows(IllegalArgumentException.class,
                () -> commentService.create(seller.getId(), invalidRequest));
    }

    /**
     * Test retrieves all comments for a seller as an admin user.
     * Arrange: Create two comments with different approval statuses
     *         Mock user repository to return true for seller existence
     *         Mock comment repository to return list of comments by seller ID
     * Act: Call getAllBySellerId with seller ID and admin flag set to true
     * Assert: Verify response size matches expected number of comments
     *         Verify user repository and comment repository methods were called
     */
    @Test
    @DisplayName("Get comments by seller ID for admin")
    void testGetCommentsBySellerIdAdmin() {
        // Arrange
        Comment comment1 = Comment.builder()
                .isApproved(true)
                .author(author)
                .seller(seller)
                .message("Test comment 1")
                .grade(5)
                .build();
        
        Comment comment2 = Comment.builder()
                .isApproved(false)
                .author(author)
                .seller(seller)
                .message("Test comment 2")
                .grade(4)
                .build();

        when(userRepository.existsById(seller.getId())).thenReturn(true);
        when(commentRepository.findAllBySellerId(seller.getId()))
                .thenReturn(Arrays.asList(comment1, comment2));

        // Act
        List<CommentResponseDTO> response = commentService.getAllBySellerId(seller.getId(), true);

        // Assert
        assertEquals(2, response.size());
        verify(userRepository).existsById(seller.getId());
        verify(commentRepository).findAllBySellerId(seller.getId());
    }

    /**
     * Test that deleting comment which is not exists
     * Arrange: Mock comment repository to return empty result for comment by ID and seller ID
     * Act & Assert: Verify ResourceNotFoundException is thrown
     */
    @Test
    @DisplayName("Delete comment not found throws exception")
    void testDeleteCommentNotFound() {
        // Arrange
        when(commentRepository.findByIdAndSellerId(1L, seller.getId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.delete(seller.getId(), 1L));
    }

    /**
     * Test that updates comment
     * Arrange: Create existing comment
     *         Create comment update request
     *         Mock user repository to return seller
     *         Mock comment repository to return existing comment by ID and seller ID
     *         Mock authorization service to return author
     *         Mock comment repository save operation
     * Act: Call update with seller ID, comment ID, and update request
     * Assert: Verify response message matches updated message
     *         Verify response status matches updated status
     *         Verify comment was saved
     */
    @Test
    @DisplayName("Update comment content")
    void testUpdateCommentContent() {
        // Arrange
        Comment existingComment = Comment.builder()
                .id(1L)
                .message("Old message")
                .grade(4)
                .seller(seller)
                .author(author)
                .build();

        CommentRequestDTO updateRequest = new CommentRequestDTO("Updated message", 4);

        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));
        when(commentRepository.findByIdAndSellerId(1L, seller.getId()))
                .thenReturn(Optional.of(existingComment));
        when(authorizationService.getCurrentUser()).thenReturn(author);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(existingComment);

        // Act
        CommentResponseDTO response = commentService.update(seller.getId(), 1L, updateRequest);

        // Assert
        assertEquals("Updated message", response.message());
        assertEquals(Status.UPDATED.getValueOfStatus(), response.status());
        verify(commentRepository).save(any(Comment.class));
    }

    /**
     * Test that rejecting comment approval
     * Arrange: Create comment with isApproved set to false
     *         Mock user repository to return true for seller existence
     *         Mock comment repository to return comment by ID and seller ID
     * Act: Call approveComment with seller ID, comment ID, and approval status set to false
     * Assert: Verify comment was not saved
     *         Verify comment was deleted
     *         Verify seller rating was updated
     */
    @Test
    @DisplayName("Reject comment approval")
    void testRejectComment() {
        // Arrange
        Comment comment = Comment.builder()
                .id(1L)
                .message("Test comment")
                .seller(seller)
                .author(author)
                .isApproved(false)
                .build();

        when(userRepository.existsById(seller.getId())).thenReturn(true);
        when(commentRepository.findByIdAndSellerId(1L, seller.getId()))
                .thenReturn(Optional.of(comment));

        // Act
        commentService.approveComment(seller.getId(), 1L, false);

        // Assert
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentRepository).delete(comment);
        verify(ratingCalculationServiceImplementation).updateSellerRating(seller.getId());
    }

    /**
     * Test retrieves single comment by ID (command is executed by admin)
     * Arrange: Create comment with isApproved set to false
     *         Mock comment repository to return comment by ID and seller ID
     *         Mock authorization service to return author
     * Act: Call getComment with seller ID, comment ID, and admin flag set to true
     * Assert: Verify response is not null
     *         Verify response message matches expected message
     */
    @Test
    @DisplayName("Get single comment by ID for admin")
    void testGetCommentByIdAdmin() {
        // Arrange
        Comment comment = Comment.builder()
                .id(1L)
                .message("Test comment")
                .seller(seller)
                .author(author)
                .isApproved(false)
                .build();

        when(commentRepository.findByIdAndSellerId(1L, seller.getId()))
                .thenReturn(Optional.of(comment));
        when(authorizationService.getCurrentUser()).thenReturn(author);

        // Act
        CommentResponseDTO response = commentService.getComment(seller.getId(), 1L, true);

        //Assert
        assertNotNull(response);
        assertEquals("Test comment", response.message());
    }

    /**
     * Test retrieves single comment by ID (command is executed by regular user)
     * Arrange: Create comment with isApproved set to true
     *         Mock comment repository to return comment by ID and seller ID
     *         Mock authorization service to return author
     * Act: Call getComment with seller ID, comment ID, and admin flag set to false
     * Assert: Verify response is not null
     *         Verify response status matches approved status
     */
    @Test
    @DisplayName("Get single comment by ID for regular user")
    void testGetCommentByIdUser() {
        // Arrange
        Comment comment = Comment.builder()
                .id(1L)
                .message("Test comment")
                .seller(seller)
                .author(author)
                .isApproved(true)
                .build();

        when(commentRepository.findByIdAndSellerId(1L, seller.getId()))
                .thenReturn(Optional.of(comment));
        when(authorizationService.getCurrentUser()).thenReturn(author);

        // Act
        CommentResponseDTO response = commentService.getComment(seller.getId(), 1L, false);

        // Assert
        assertNotNull(response);
        assertEquals(Status.APPROVED.getValueOfStatus(), response.status());
    }

    /**
     * Test which updates comment (executed by unauthorized user) throws exception
     * Arrange: Create existing comment
     *         Mock user repository to return seller
     *         Mock comment repository to return existing comment by ID and seller ID
     *         Mock authorization service to return null
     *         Mock authorization service to throw ForbiddenException
     * Act: Create comment update request
     * Assert: Verify ForbiddenException is thrown
     */
    @Test
    @DisplayName("Update comment with unauthorized user throws exception")
    void testUpdateCommentUnauthorized() {
        // Arrange
        Comment existingComment = Comment.builder()
                .id(1L)
                .message("Original message")
                .seller(seller)
                .author(author)
                .build();

        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(commentRepository.findByIdAndSellerId(1L, seller.getId()))
                .thenReturn(Optional.of(existingComment));
        when(commentRepository.findById(existingComment.getId()))
                .thenReturn(Optional.of(existingComment));
        when(authorizationService.getCurrentUser()).thenReturn(null);
        doThrow(new ForbiddenException("Unauthorized"))
                .when(authorizationService)
                .authorizeResourceModification(any(), any());

        // Act
        CommentRequestDTO updateRequest = new CommentRequestDTO("Updated message", 4);

        // Assert
        assertThrows(ForbiddenException.class,
                () -> commentService.update(seller.getId(), 1L, updateRequest));
    }
}