package org.leverx.ratingapp.services.rating;

import lombok.RequiredArgsConstructor;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.entities.SellerRating;
import org.leverx.ratingapp.exceptions.ResourceNotFoundException;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.SellerRatingRepository;
import org.leverx.ratingapp.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingCalculationServiceImplementation implements RatingCalculationService {
    private final CommentRepository commentRepository;
    private final SellerRatingRepository sellerRatingRepository;
    private final UserRepository userRepository;

    @Transactional
    public void updateSellerRating(Long sellerId) {
        SellerRating sellerRating = sellerRatingRepository.findByUserId(sellerId)
                .orElseGet(() -> {
                    SellerRating newRating = SellerRating.builder()
                            .user(userRepository.findById(sellerId)
                                    .orElseThrow(() -> new ResourceNotFoundException("User not found")))
                            .build();
                    return sellerRatingRepository.save(newRating);
                });

        List<Comment> approvedComments = commentRepository.findAllBySellerId(sellerId).stream()
                .filter(Comment::getIsApproved)
                .toList();

        int totalRatings = approvedComments.size();
        double averageRating = approvedComments.isEmpty()
                ? 0.0
                : approvedComments.stream()
                    .mapToInt(Comment::getGrade)
                    .average()
                    .orElse(0.0);

        sellerRating.setTotalComments(totalRatings);
        sellerRating.setAverageRating(averageRating);
        sellerRating.setRating((int) Math.round(averageRating));
        
        sellerRatingRepository.save(sellerRating);
    }

    public Double getSellerRating(Long sellerId) {
        return sellerRatingRepository.findByUserId(sellerId)
                .map(SellerRating::getAverageRating)
                .orElse(0.0);
    }

    public Integer getNumberOfRatings(Long sellerId) {
        return sellerRatingRepository.findByUserId(sellerId)
                .map(SellerRating::getTotalComments)
                .orElse(0);
    }
}
