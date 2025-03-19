package org.leverx.ratingapp.services.email.templates;

/**
 * EmailTemplateService is an interface that provides functionalities to build HTML content for email templates.
 */
public interface EmailTemplateService {
    // Builds the HTML content for the registration email.
    String buildRegistrationEmail(String name, String link);
    // Builds the HTML content for the password reset email.
    String buildPasswordResetEmail(String name, String code);
}