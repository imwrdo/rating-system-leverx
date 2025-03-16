package org.leverx.ratingapp.services.email.validation;

import org.springframework.stereotype.Service;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Service class responsible for validating email addresses.
 * Implements the {@link Predicate} interface to provide an email validation check.
 * Utilizes a regular expression to match a valid email format.
 */
@Service
public class EmailValidatorService implements Predicate<String> {

    // Regular expression pattern for validating an email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Validates whether the provided email string matches the valid email format.
     * This method is used as part of the Predicate interface implementation.
     *
     * @param email The email address to validate.
     * @return true if the email is not null and matches the valid email format, false otherwise.
     */
    @Override
    public boolean test(String email) {
        // Check if the email is not null and matches the regex pattern for a valid email
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}