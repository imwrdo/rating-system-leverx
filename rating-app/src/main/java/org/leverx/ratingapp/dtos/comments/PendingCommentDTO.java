package org.leverx.ratingapp.dtos.comments;

import lombok.Builder;

@Builder
public record PendingCommentDTO (Long sellerId, String message){
}
