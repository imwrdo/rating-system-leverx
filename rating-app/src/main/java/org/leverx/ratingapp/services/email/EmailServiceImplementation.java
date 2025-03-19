package org.leverx.ratingapp.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.exceptions.InvalidOperationException;
import org.leverx.ratingapp.services.email.templates.EmailTemplateService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link EmailService} interface that handles sending registration
 * and password reset emails to users.
 * This service uses {@link JavaMailSender} for sending emails .
 * It includes email construction methods and handles errors related to email sending.
 *
 */
@Service
@AllArgsConstructor
public class EmailServiceImplementation implements EmailService {
    // Dependency injection of JavaMailSender for sending emails
    private final JavaMailSender mailSender;
    // Dependency injection of EmailTemplateService for building email templates
    private final EmailTemplateService emailTemplate;

    /**
     * Sends a registration email to the user with an activation link.
     * This method is asynchronous and will send the email in the background.
     *
     * @param to The recipient's email address.
     * @param name The recipient's name.
     * @param link The activation link for email verification.
     */
    @Override
    @Async
    public void sendRegistrationEmail(String to, String name,String link) {
        sendEmail(to, "Confirm your email", emailTemplate.buildRegistrationEmail(name, link), "Failed to send email");
    }

    /**
     * Sends a password reset email to the user with a reset code.
     * This method is asynchronous and will send the email in the background.
     *
     * @param to The recipient's email address.
     * @param name The recipient's name.
     * @param code The password reset code.
     */
    @Override
    @Async
    public void sendPasswordResetEmail(String to, String name, String code) {
        sendEmail(to,"Password Reset Request", emailTemplate.buildPasswordResetEmail(name, code), "Failed to send password reset email");
    }

    /**
     * Sends an email using the provided parameters.
     *
     * @param to The recipient's email address
     * @param subject The email subject
     * @param content The HTML content of the email
     * @param errorMessage The error message to use if sending fails
     */
    private void sendEmail(String to, String subject, String content, String errorMessage) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setText(content, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(System.getenv("MAIL_USERNAME"));
            mailSender.send(message);
        } catch(MessagingException e) {
            throw new InvalidOperationException(errorMessage);
        }
    }

}
