package org.leverx.ratingapp.services.rating;

/**
 * Handles updating seller ratings based on approved comments and fetching rating details
 */
public interface RatingCalculationService {
    // Updates the seller rating by calculating the average rating based on approved comments
    void updateSellerRating(Long sellerId);

    // Retrieves the average rating of a seller
    Double getSellerRating(Long sellerId);

    // Retrieves the total number of comments (ratings) for a seller
    Integer getNumberOfRatings(Long sellerId);
}
