package org.leverx.ratingapp.services.email;

/**
 * Handles sending registration and password reset emails to users.
 */
public interface EmailService {
    // Sends a registration email to the user with an activation link.
    void sendRegistrationEmail(String to, String email,String link);

    // Sends a password reset email to the user with a reset code.
    void sendPasswordResetEmail(String to, String name, String code);

    // Builds the HTML content for the registration email
    String buildRegistrationEmail(String name, String link);

    // Builds the HTML content for the password reset email
    String buildPasswordResetEmail(String name, String code);
}
