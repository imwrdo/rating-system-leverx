package org.leverx.ratingapp.repositories;

import org.leverx.ratingapp.models.entities.SellerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRatingRepository extends JpaRepository<SellerRating, Long> {
    Optional<SellerRating> findByUserId(Long userId);
}
