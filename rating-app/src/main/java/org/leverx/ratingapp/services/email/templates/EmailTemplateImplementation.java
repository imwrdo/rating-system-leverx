package org.leverx.ratingapp.services.email.templates;

import org.springframework.stereotype.Service;

/**
 * EmailTemplateImplementation is the concrete implementation of the {@link EmailTemplateService} interface.
 * It provides functionalities to build HTML content for email templates.
 */
@Service
public class EmailTemplateImplementation implements EmailTemplateService{

    /**
     * Builds the HTML content for the registration email.
     *
     * @param name The recipient's name.
     * @param link The activation link for email verification.
     * @return The HTML content for the registration email.
     */
    @Override
    public String buildRegistrationEmail(String name, String link) {
        return """
            <div style="font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c">
                <!-- Registration email template HTML -->
                <p style="Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">Hi %s,</p>
                <p style="Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">
                    Thank you for registering. Please click on the below link to activate your account:
                </p>
                <blockquote style="Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px">
                    <p style="Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">
                        <a href="%s">Activate Now</a>
                    </p>
                </blockquote>
                Link will expire in 15 minutes. <p>See you soon</p>
            </div>
            """.formatted(name, link);
    }

    /**
     * Builds the HTML content for the password reset email.
     *
     * @param name The recipient's name.
     * @param code The password reset code.
     * @return The HTML content for the password reset email.
     */
    @Override
    public String buildPasswordResetEmail(String name, String code) {
        return """
            <div style="font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c">
                <!-- Password reset email template HTML -->
                <p style="Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">Hi %s,</p>
                <p style="margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c;">
                    We know that you are trying to reset your password. Your password reset code is: <strong>"%s"</strong>
                </p>
                <p style="margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c;">
                    This code will expire in 15 minutes.
                </p>
                <p>See you soon</p>
            </div>
            """.formatted(name, code);
    }
}
