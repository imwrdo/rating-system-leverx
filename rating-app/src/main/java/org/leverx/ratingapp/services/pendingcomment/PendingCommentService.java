package org.leverx.ratingapp.services.pendingcomment;


public interface PendingCommentService {
    void savePendingComment(String email, Long sellerId, String comment, Integer grade);
    void processPendingComment(String email);
}
