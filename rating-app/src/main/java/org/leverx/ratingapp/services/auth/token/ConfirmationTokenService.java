package org.leverx.ratingapp.services.auth.token;

import java.util.Optional;

/**
 * ConfirmationTokenService interface defines the essential operations related to
 * handling confirmation tokens and reset codes for user authentication processes.
 */
public interface ConfirmationTokenService {
     // Saves a confirmation token for a specific user email.
     void saveConfirmationToken(String email, String token);

     // Retrieves the confirmation token associated with a user's email
     Optional<String> getConfirmationToken(String email);

     // Removes the confirmation token for a user's email
     void removeConfirmationToken(String email);

}
