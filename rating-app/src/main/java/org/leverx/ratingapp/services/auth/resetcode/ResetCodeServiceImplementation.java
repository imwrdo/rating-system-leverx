package org.leverx.ratingapp.services.auth.resetcode;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.repositories.redis.ResetCodeRepository;
import org.springframework.stereotype.Service;

/**
 * ResetCodeServiceImplementation is the concrete implementation of the
 * {@link ResetCodeService} interface.
 * It provides functionalities to handle reset codes for user authentication processes.
 */
@Service
@AllArgsConstructor
public class ResetCodeServiceImplementation implements ResetCodeService {
    private final ResetCodeRepository resetCodeRepository;
    /**
     * Saves a reset code for a specific user's email.
     * The reset code is typically used for password reset functionality.
     *
     * @param email The user's email address.
     * @param resetCode The reset code to be saved.
     */
    @Override
    public void saveResetCode(String email, String resetCode) {
        resetCodeRepository.saveResetCode(email, resetCode);
    }

    /**
     * Retrieves the reset code associated with a user's email.
     * Used for validating password reset requests from the user.
     *
     * @param email The user's email address.
     * @return The reset code if found.
     */
    @Override
    public String getResetCode(String email) {
        return resetCodeRepository.getResetCode(email);
    }

    /**
     * Removes the reset code associated with a user's email.
     * This is typically done after the reset code has been used or has expired.
     *
     * @param email The user's email address.
     */
    @Override
    public void removeResetCode(String email) {
        resetCodeRepository.removeResetCode(email);
    }
}
