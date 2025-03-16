package org.leverx.ratingapp.services.rating;

public interface RatingCalculationService {
    void updateSellerRating(Long sellerId);
    Double getSellerRating(Long sellerId);
    Integer getNumberOfRatings(Long sellerId);
}
