package org.leverx.ratingapp.services.rating;

import lombok.RequiredArgsConstructor;
import org.leverx.ratingapp.models.entities.Comment;
import org.leverx.ratingapp.models.entities.SellerRating;
import org.leverx.ratingapp.exceptions.ResourceNotFoundException;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.SellerRatingRepository;
import org.leverx.ratingapp.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation of {@link RatingCalculationService} for calculating and managing seller ratings.
 * This service handles updating seller ratings based on approved comments and fetching rating details.
 */
@Service
@RequiredArgsConstructor
public class RatingCalculationServiceImplementation implements RatingCalculationService {
    private final CommentRepository commentRepository;
    private final SellerRatingRepository sellerRatingRepository;
    private final UserRepository userRepository;

    /**
     * Updates the seller rating by calculating the average rating based on approved comments.
     * It also updates the total number of approved comments for the seller.
     * If the seller doesn't already have a rating record, it creates one.
     *
     * @param sellerId The ID of the seller whose rating needs to be updated.
     * @throws ResourceNotFoundException If the seller is not found in the user repository.
     */
    @Transactional
    @Override
    public void updateSellerRating(Long sellerId) {
        // Fetch the existing seller rating, or create a new one if it doesn't exist
        SellerRating sellerRating = sellerRatingRepository.findByUserId(sellerId)
                .orElseGet(() -> {
                    // If no existing rating, create a new seller rating object
                    SellerRating newRating = SellerRating.builder()
                            .user(userRepository.findById(sellerId)
                                    .orElseThrow(() -> new ResourceNotFoundException("User not found")))
                            .build();
                    return sellerRatingRepository.save(newRating);
                });
        // Fetch all approved comments for the seller and filter the approved ones
        List<Comment> approvedComments = commentRepository.findAllBySellerId(sellerId).stream()
                .filter(Comment::getIsApproved)
                .toList();

        // Calculate the total number of approved comments
        int totalRatings = approvedComments.size();

        // Calculate the average rating for the approved comments
        double averageRating = approvedComments.isEmpty()
                ? 0.0  // If no approved comments, set average rating to 0
                : approvedComments.stream()
                .mapToInt(Comment::getGrade) // Get the grade of each comment
                .average()  // Calculate the average of the grades
                .orElse(0.0);

        // Update the seller rating with the calculated values
        sellerRating.setTotalComments(totalRatings);
        sellerRating.setAverageRating(averageRating);
        sellerRating.setRating((int) Math.round(averageRating)); // Round the average rating to the nearest integer

        // Save the updated seller rating
        sellerRatingRepository.save(sellerRating);
    }

    /**
     * Retrieves the average rating of a seller.
     *
     * @param sellerId The ID of the seller whose rating is to be fetched.
     * @return The average rating of the seller, or 0.0 if no rating exists.
     */
    @Override
    public Double getSellerRating(Long sellerId) {
        // Fetch the average rating for the seller, or return 0 if no rating exists
        return sellerRatingRepository.findByUserId(sellerId)
                .map(SellerRating::getAverageRating)
                .orElse(0.0);
    }

    /**
     * Retrieves the total number of comments (ratings) for a seller.
     *
     * @param sellerId The ID of the seller whose comment count is to be fetched.
     * @return The total number of comments for the seller, or 0 if no ratings exist.
     */
    @Override
    public Integer getNumberOfRatings(Long sellerId) {
        // Fetch the total number of comments for the seller, or return 0 if no ratings exist
        return sellerRatingRepository.findByUserId(sellerId)
                .map(SellerRating::getTotalComments)
                .orElse(0);
    }
}
