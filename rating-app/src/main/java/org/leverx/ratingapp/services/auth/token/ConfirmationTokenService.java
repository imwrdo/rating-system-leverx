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

     // Saves a reset code for a specific user's email
     void saveResetCode(String email, String resetCode);

     // Retrieves the reset code associated with a user's email
     String getResetCode(String email);

     // Removes the reset code associated with a user's email.
     void removeResetCode(String email);
}
