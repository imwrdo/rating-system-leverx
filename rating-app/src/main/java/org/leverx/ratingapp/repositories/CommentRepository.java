package org.leverx.ratingapp.repositories;

import org.leverx.ratingapp.models.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.seller.id = ?1")
    List<Comment> findAllBySellerId(Long sellerId);

    Optional<Comment> findByIdAndSellerId(Long commentId, Long sellerId);

    @Query("SELECT c FROM Comment c WHERE c.isApproved = true")
    List<Comment> findAllAcceptedBySellerId(Long sellerId);
}
