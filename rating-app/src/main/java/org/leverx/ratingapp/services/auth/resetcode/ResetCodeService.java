package org.leverx.ratingapp.services.auth.resetcode;

/**
 * ResetCodeService interface defines the essential operations related to
 * handling reset codes for user authentication processes.
 */
public interface ResetCodeService {

    // Saves a reset code for a specific user's email
    void saveResetCode(String email, String resetCode);

    // Retrieves the reset code associated with a user's email
    String getResetCode(String email);

    // Removes the reset code associated with a user's email.
    void removeResetCode(String email);
}
