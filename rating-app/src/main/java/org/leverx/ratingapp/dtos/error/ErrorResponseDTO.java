package org.leverx.ratingapp.dtos.error;

import java.time.LocalDateTime;

public record ErrorResponseDTO(String error,String message,Integer status, LocalDateTime timestamp) {

}

