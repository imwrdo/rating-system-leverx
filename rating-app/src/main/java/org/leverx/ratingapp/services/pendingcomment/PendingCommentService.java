package org.leverx.ratingapp.services.pendingcomment;


public interface PendingCommentService {
    void savePendingComment(String email, Long sellerId, String comment);
    void processPendingComment(String email);
}
