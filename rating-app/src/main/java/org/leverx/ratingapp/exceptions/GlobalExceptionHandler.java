package org.leverx.ratingapp.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import org.leverx.ratingapp.dtos.error.ErrorResponseDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

/**
 * Global exception handler for REST controllers
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handler for generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        return createErrorResponse(
                "An unexpected error occurred",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // Handler for expired JWT exceptions
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpiredJwtException(ExpiredJwtException ex) {
        return createErrorResponse(
                "Your token is expired, please, login again",
                "Expired JWT token",
                HttpStatus.NOT_ACCEPTABLE);
    }

    // Handler for data integrity violation exceptions (e.g., database constraint violations)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return createErrorResponse("Please check that you have entered all the required data",
                "Not all data provided",
                HttpStatus.BAD_REQUEST);
    }

    // Handler for illegal argument exceptions (e.g., invalid input data)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentExceptionException(IllegalArgumentException ex) {
        return createErrorResponse("Please check provided data",
                "Bad request",
                HttpStatus.BAD_REQUEST);
    }

    // Handler for custom ConflictException (e.g., business logic conflicts)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflictException
            (ConflictException ex) {
        return createErrorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.CONFLICT);
    }

    // Handler for unsupported HTTP methods (e.g., POST instead of GET)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpRequestMethodNotSupportedException
            (HttpRequestMethodNotSupportedException ex) {
        return createErrorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handler for unauthorized access exceptions
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(UnauthorizedException ex) {
        return createErrorResponse(ex.getMessage(), "Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    // Handler for resource not found exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(ResourceNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), "Not Found", HttpStatus.NOT_FOUND);
    }

    // Handler for invalid operations
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidOperationException(InvalidOperationException ex) {
        return createErrorResponse(ex.getMessage(), "Bad Request", HttpStatus.BAD_REQUEST);
    }

    // Handler for bad credentials (authentication errors)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentialsException(BadCredentialsException ex) {
        return createErrorResponse(ex.getMessage(), "Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    // Handler for forbidden access exceptions
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbiddenException(ForbiddenException ex) {
        return createErrorResponse(ex.getMessage(), "Forbidden", HttpStatus.FORBIDDEN);
    }

    // Handler for account not activated exceptions
    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountNotActivatedException(AccountNotActivatedException ex) {
        return createErrorResponse(ex.getMessage(), "Account is not activated", HttpStatus.BAD_REQUEST);
    }

    // Handler for username not found exceptions (authentication issues)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), "Username is not found", HttpStatus.NOT_FOUND);
    }

    // Helper method for creating error responses
    private ResponseEntity<ErrorResponseDTO> createErrorResponse(
            String message, String error, HttpStatus status) {
        return new ResponseEntity<>(
                new ErrorResponseDTO(error,
                        message,
                        status.value(),
                        LocalDateTime.now()),
                status);
    }
}
