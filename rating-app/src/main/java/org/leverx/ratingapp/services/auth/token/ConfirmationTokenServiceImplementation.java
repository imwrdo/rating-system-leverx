package org.leverx.ratingapp.services.auth.token;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.repositories.redis.ConfirmationTokenRedisRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ConfirmationTokenServiceImplementation is the concrete implementation of the
 * {@link ConfirmationTokenService} interface.
 * It provides functionalities to handle confirmation tokens and reset codes for user authentication processes.
 */
@Service
@Transactional
@AllArgsConstructor
public class ConfirmationTokenServiceImplementation implements ConfirmationTokenService {
    private final ConfirmationTokenRedisRepository confirmationTokenRedisRepository;

    /**
     * Saves a confirmation token for a specific user email.
     * Typically used to store tokens that are sent to users for email verification purposes.
     *
     * @param email The user's email address.
     * @param token The confirmation token to be saved.
     */
    @Override
    public void saveConfirmationToken(String email, String token) {
        confirmationTokenRedisRepository.save(email, token);
    }

    /**
     * Retrieves the confirmation token associated with a user's email.
     * This is used to verify if the token provided by the user matches the one stored for that email.
     *
     * @param email The user's email address.
     * @return An Optional containing the confirmation token if it exists, or empty if no token is found.
     */
    @Override
    public Optional<String> getConfirmationToken(String email) {
        return Optional.ofNullable(confirmationTokenRedisRepository.get(email));
    }

    /**
     * Removes the confirmation token for a user's email.
     * This is typically done once the confirmation process has been completed or the token has expired.
     *
     * @param email The user's email address.
     */
    @Override
    public void removeConfirmationToken(String email) {
        confirmationTokenRedisRepository.remove(email);
    }




}
