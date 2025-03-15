package org.leverx.ratingapp.exceptions;

public class ConflictException extends InvalidOperationException {
    public ConflictException(String message) {
        super(message);
    }
}
