package org.leverx.ratingapp.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.leverx.ratingapp.services.email.interfaces.EmailSender;
import org.leverx.ratingapp.exceptions.InvalidOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements EmailSender {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void send(String to, String email) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("nasznetflixgdansk@gmail.com");
            mailSender.send(message);
        }catch(MessagingException e){
            LOGGER.error("Failed to send email ", e);
            throw new InvalidOperationException("Failed to send email");
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            String emailContent = String.format(
                "Your password reset code is: %s\nThis code will expire in 15 minutes.",
                code
            );
            helper.setText(emailContent, true);
            helper.setTo(to);
            helper.setSubject("Password Reset Request");
            helper.setFrom("nasznetflixgdansk@gmail.com");
            mailSender.send(message);
        } catch(MessagingException e) {
            LOGGER.error("Failed to send password reset email ", e);
            throw new InvalidOperationException("Failed to send password reset email");
        }
    }

}
