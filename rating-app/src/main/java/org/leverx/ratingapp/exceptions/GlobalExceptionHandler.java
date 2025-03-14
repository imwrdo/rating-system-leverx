package org.leverx.ratingapp.exceptions;

import org.leverx.ratingapp.dtos.error.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        return createErrorResponse(
                "An unexpected error occurred",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    public ResponseEntity<ErrorResponseDTO> handleHttpRequestMethodNotSupportedException
            (HttpRequestMethodNotSupportedException ex) {
        return createErrorResponse(ex.getMessage(), ex.getLocalizedMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(UnauthorizedException ex) {
        return createErrorResponse(ex.getMessage(), "Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(ResourceNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), "Not Found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidOperationException(InvalidOperationException ex) {
        return createErrorResponse(ex.getMessage(), "Bad Request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentialsException(BadCredentialsException ex) {
        return createErrorResponse(ex.getMessage(), "Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbiddenException(ForbiddenException ex) {
        return createErrorResponse(ex.getMessage(), "Forbidden", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccountNotActivatedException(AccountNotActivatedException ex) {
        return createErrorResponse(ex.getMessage(), "Account is not activated", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), "Username is not found", HttpStatus.NOT_FOUND);
    }

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
