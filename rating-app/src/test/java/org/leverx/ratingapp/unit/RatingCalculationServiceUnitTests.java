package org.leverx.ratingapp.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.leverx.ratingapp.models.entities.Comment;
import org.leverx.ratingapp.models.entities.SellerRating;
import org.leverx.ratingapp.models.entities.User;
import org.leverx.ratingapp.exceptions.ResourceNotFoundException;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.SellerRatingRepository;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.rating.RatingCalculationServiceImplementation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 * Unit tests for the {@link RatingCalculationServiceImplementation class}.
 * This class tests various functionalities of the rating calculation service,
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Rating Calculation Service Unit Tests")
class RatingCalculationServiceUnitTests {

    @Mock private CommentRepository commentRepository;
    @Mock private SellerRatingRepository sellerRatingRepository;
    @Mock private UserRepository userRepository;
    @Mock private User seller;

    @InjectMocks
    private RatingCalculationServiceImplementation ratingService;

    /**
     * Sets up test data before each test case.
     */
    @BeforeEach
    void setUp() {
        seller = User.builder()
                .id(1L)
                .email("seller@test.com")
                .build();
    }

    /**
     * Test case for updating the seller's rating based on approved comments.
     * Arrange: Create test comments with different grades and approval status,
     *         Set up seller rating entity with basic data,
     *         Mock repository responses for finding and saving ratings
     * Act: Call updateSellerRating with seller ID
     * Assert: Verify the saved rating has correct average (4.5), 
     *        final rating (5), and total comments count (2)
     */
    @Test
    @DisplayName("Update seller rating with new approved comments")
    void testUpdateSellerRating() {
        // Arrange
        Comment comment1 = Comment.builder().grade(5).isApproved(true).build();
        Comment comment2 = Comment.builder().grade(4).isApproved(true).build();
        Comment comment3 = Comment.builder().grade(3).isApproved(false).build();

        SellerRating sellerRating = SellerRating.builder()
                .id(1L)
                .user(seller)
                .build();

        when(sellerRatingRepository.findByUserId(seller.getId()))
                .thenReturn(Optional.of(sellerRating));
        when(commentRepository.findAllBySellerId(seller.getId()))
                .thenReturn(Arrays.asList(comment1, comment2, comment3));
        when(sellerRatingRepository.save(any(SellerRating.class)))
                .thenReturn(sellerRating);

        // Act
        ratingService.updateSellerRating(seller.getId());

        // Assert
        verify(sellerRatingRepository).save(argThat(rating ->
                rating.getAverageRating() == 4.5 &&
                        rating.getRating() == 5 &&
                        rating.getTotalComments() == 2
        ));
    }

    /**
     * Test case for getting the seller's rating when no comments exist.
     * Arrange: Mock repository to return empty rating
     * Act: Get seller rating for the test seller ID
     * Assert: Verify returned rating is 0.0
     */
    @Test
    @DisplayName("Get seller rating when no comments exist")
    void testGetSellerRatingNoComments() {
        // Arrange
        when(sellerRatingRepository.findByUserId(seller.getId()))
                .thenReturn(Optional.empty());

        // Act
        Double rating = ratingService.getSellerRating(seller.getId());

        // Assert
        assertEquals(0.0, rating);
    }

    /**
     * Test case for getting the number of ratings for a seller.
     * Arrange: Create seller rating with 5 total comments,
     *         Mock repository to return the rating
     * Act: Get number of ratings for seller
     * Assert: Verify returned count matches expected 5 ratings
     */
    @Test
    @DisplayName("Get number of ratings for seller")
    void testGetNumberOfRatings() {
        // Arrange
        SellerRating sellerRating = SellerRating.builder()
                .totalComments(5)
                .build();
        when(sellerRatingRepository.findByUserId(seller.getId()))
                .thenReturn(Optional.of(sellerRating));

        // Act
        Integer totalRatings = ratingService.getNumberOfRatings(seller.getId());

        // Assert
        assertEquals(5, totalRatings);
    }

    /**
     * Test case for calculating average rating with multiple grades.
     * Arrange: Create three approved comments with grades 5, 3, and 4,
     *         Mock repositories for finding seller and comments
     * Act: Update seller rating
     * Assert: Verify saved rating has correct average (4.0) and total comments (3)
     */
    @Test
    @DisplayName("Calculate average rating with multiple grades")
    void testCalculateAverageRating() {
        // Arrange
        Comment comment1 = Comment.builder().grade(5).isApproved(true).build();
        Comment comment2 = Comment.builder().grade(3).isApproved(true).build();
        Comment comment3 = Comment.builder().grade(4).isApproved(true).build();

        when(sellerRatingRepository.findByUserId(seller.getId()))
                .thenReturn(Optional.of(SellerRating.builder().user(seller).build()));
        when(commentRepository.findAllBySellerId(seller.getId()))
                .thenReturn(Arrays.asList(comment1, comment2, comment3));

        // Act
        ratingService.updateSellerRating(seller.getId());

        // Assert
        verify(sellerRatingRepository).save(argThat(rating ->
                rating.getAverageRating() == 4.0 &&
                        rating.getTotalComments() == 3
        ));
    }

    /**
     * Test case for handling seller with no approved comments.
     * Arrange: Create two unapproved comments,
     *         Mock repositories to return unapproved comments
     * Act: Update seller rating
     * Assert: Verify rating is 0.0 and total comments is 0
     */
    @Test
    @DisplayName("Handle seller with no approved comments")
    void testNoApprovedComments() {
        // Arrange
        Comment comment1 = Comment.builder().grade(5).isApproved(false).build();
        Comment comment2 = Comment.builder().grade(4).isApproved(false).build();

        when(sellerRatingRepository.findByUserId(seller.getId()))
                .thenReturn(Optional.of(SellerRating.builder().user(seller).build()));
        when(commentRepository.findAllBySellerId(seller.getId()))
                .thenReturn(Arrays.asList(comment1, comment2));
        // Act
        ratingService.updateSellerRating(seller.getId());

        // Assert
        verify(sellerRatingRepository).save(argThat(rating ->
                rating.getAverageRating() == 0.0 &&
                        rating.getTotalComments() == 0
        ));
    }

    /**
     * Test case for updating existing rating with new comments.
     * Arrange: Create existing rating with 4.0 average and 2 comments,
     *         Create new approved comment with grade 5,
     *         Mock repositories
     * Act: Update seller rating
     * Assert: Verify new average is 5.0 with 1 total comment
     */
    @Test
    @DisplayName("Update existing rating with new comments")
    void testUpdateExistingRating() {
        // Arrange
        SellerRating existing = SellerRating.builder()
                .user(seller)
                .averageRating(4.0)
                .totalComments(2)
                .build();

        Comment newComment = Comment.builder().grade(5).isApproved(true).build();

        when(sellerRatingRepository.findByUserId(seller.getId()))
                .thenReturn(Optional.of(existing));
        when(commentRepository.findAllBySellerId(seller.getId()))
                .thenReturn(Arrays.asList(newComment));
        // Act
        ratingService.updateSellerRating(seller.getId());

        // Assert
        verify(sellerRatingRepository).save(argThat(rating ->
                rating.getAverageRating() == 5.0 &&
                        rating.getTotalComments() == 1
        ));
    }

    /**
     * Test case for handling seller not found scenario.
     * Arrange: Mock repositories to return empty for seller lookup
     * Act & Assert: Verify ResourceNotFoundException is thrown when
     *              updating rating for non-existent seller
     */
    @Test
    @DisplayName("Handle seller not found exception")
    void testSellerNotFound() {
        // Arrange
        when(sellerRatingRepository.findByUserId(seller.getId()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(seller.getId()))
                .thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                ratingService.updateSellerRating(seller.getId()));
    }

    /**
     * Test case for getting rating with multiple existing ratings.
     * Arrange: Create rating entity with 4.5 average,
     *         Mock repository to return the rating
     * Act: Get seller rating
     * Assert: Verify returned rating matches expected 4.5
     */
    @Test
    @DisplayName("Get rating for seller with multiple ratings")
    void testGetMultipleRatings() {
        // Arrange
        SellerRating rating = SellerRating.builder()
                .averageRating(4.5)
                .build();
        when(sellerRatingRepository.findByUserId(seller.getId()))
                .thenReturn(Optional.of(rating));

        // Act
        Double result = ratingService.getSellerRating(seller.getId());

        // Assert
        assertEquals(4.5, result);
    }

    /**
     * Test case for rounding average rating calculation.
     * Arrange: Create two approved comments with grades 4 and 5,
     *         Mock repositories for finding seller and comments
     * Act: Update seller rating
     * Assert: Verify final rating is rounded to 5 (from 4.5)
     */
    @Test
    @DisplayName("Round average rating to nearest integer")
    void testRoundAverageRating() {
        // Arrange
        Comment comment1 = Comment.builder().grade(4).isApproved(true).build();
        Comment comment2 = Comment.builder().grade(5).isApproved(true).build();

        when(sellerRatingRepository.findByUserId(seller.getId()))
                .thenReturn(Optional.of(SellerRating.builder().user(seller).build()));
        when(commentRepository.findAllBySellerId(seller.getId()))
                .thenReturn(Arrays.asList(comment1, comment2));

        // Act
        ratingService.updateSellerRating(seller.getId());

        // Assert
        verify(sellerRatingRepository).save(argThat(rating ->
                rating.getRating() == 5 // 4.5 rounded up to 5
        ));
    }
}
