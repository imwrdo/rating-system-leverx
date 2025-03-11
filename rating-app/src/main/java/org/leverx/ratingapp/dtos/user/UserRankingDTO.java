package org.leverx.ratingapp.dtos.user;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserRankingDTO(
        Long place,
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt,
        int commentCount
) {}
