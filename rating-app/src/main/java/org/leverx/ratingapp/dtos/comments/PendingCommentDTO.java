package org.leverx.ratingapp.dtos.comments;

import lombok.Builder;

/**
 * DTO representing a pending comment that is awaiting approval or processing.
 */
@Builder
public record PendingCommentDTO (Long sellerId, String message,Integer grade){
}
