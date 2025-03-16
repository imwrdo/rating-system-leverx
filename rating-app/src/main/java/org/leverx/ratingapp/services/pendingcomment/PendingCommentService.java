package org.leverx.ratingapp.services.pendingcomment;

/**
 *  This service manages saving pending comments to a repository and processing them when appropriate.
 */
public interface PendingCommentService {
    // Saves a pending comment to the repository for a specific seller and user
    void savePendingComment(String email, Long sellerId, String comment, Integer grade);

    // Processes the pending comment for a specific user
    void processPendingComment(String email);
}
