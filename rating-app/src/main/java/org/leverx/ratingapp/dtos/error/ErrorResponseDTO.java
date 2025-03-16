package org.leverx.ratingapp.dtos.error;

import java.time.LocalDateTime;
/**
 * DTO representing an error response returned to the client.
 */
public record ErrorResponseDTO(String error,String message,Integer status, LocalDateTime timestamp) {

}

