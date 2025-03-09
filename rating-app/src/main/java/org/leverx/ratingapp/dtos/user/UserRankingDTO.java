package org.leverx.ratingapp.dtos.user;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserRankingDTO(
        int place,
        Long id,
        String first_name,
        String last_name,
        String email,
        LocalDateTime created_at,
        int commentCount
) {}
