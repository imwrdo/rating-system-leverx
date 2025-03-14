package org.leverx.ratingapp.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.leverx.ratingapp.dtos.error.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Custom authentication entry point to handle unauthorized access attempts.
 * Returns a structured JSON response with an error message and HTTP status.
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Handles authentication exceptions by sending a structured JSON response.
     *
     * @param request       The HTTP request.
     * @param response      The HTTP response.
     * @param authException The authentication exception that triggered this handler.
     * @throws IOException If an I/O error occurs while writing the response.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        // Construct an error response DTO with relevant details
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "Unauthorized",
                "Access denied! Please login or register first to access this resource",
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );

        // Set HTTP response status and content type
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Configure ObjectMapper to properly handle Java 8 time types
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Write the error response as JSON to the response output stream
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
