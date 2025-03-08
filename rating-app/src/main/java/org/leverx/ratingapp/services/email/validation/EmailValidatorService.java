package org.leverx.ratingapp.services.email.validation;

import org.springframework.stereotype.Service;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class EmailValidatorService implements Predicate<String> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    @Override
    public boolean test(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
