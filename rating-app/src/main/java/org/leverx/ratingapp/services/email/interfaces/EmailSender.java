package org.leverx.ratingapp.services.email.interfaces;

public interface EmailSender {
    void send(String to, String email);
    void sendPasswordResetEmail(String to, String code);
}
