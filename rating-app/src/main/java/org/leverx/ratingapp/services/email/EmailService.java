package org.leverx.ratingapp.services.email;

public interface EmailService {
    void sendRegistrationEmail(String to, String email,String link);
    void sendPasswordResetEmail(String to, String name, String code);
    String buildRegistrationEmail(String name, String link);
    String buildPasswordResetEmail(String name, String code);
}
