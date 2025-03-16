package org.leverx.ratingapp.services.auth;

import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.auth.AuthenticationResponseDTO;
import org.leverx.ratingapp.dtos.auth.PasswordResetRequestDTO;
import org.leverx.ratingapp.dtos.auth.registration.RegistrationRequestDTO;
import org.leverx.ratingapp.models.entities.User;

/**
 * AuthenticationAndRegistrationService interface provides methods for user authentication,
 * registration, and related processes. This service manages the lifecycle of user accounts,
 * including registration, login, email confirmation, password reset, and user authorization.
 */
public interface AuthenticationAndRegistrationService {
     // Retrieves the current authenticated user
     User getCurrentUser();

     // Authorizes the user to perform an action on a given entity
     <T> void authorizeUser(T entity, User currentUser);

     // Registers a new user with the provided registration details
     AuthenticationResponseDTO register(RegistrationRequestDTO registrationRequestDTO);

     // Authenticates a user with the provided credentials
     AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);

     // Confirms the user's email by validating the provided token
     String confirmEmail(String token);

     // Initiates a password reset by sending a reset code to the user's email
     AuthenticationResponseDTO initiatePasswordReset(String email);

     // Resets the user's password using the provided new password and reset code
     AuthenticationResponseDTO resetPassword(PasswordResetRequestDTO request);

     // Verifies the provided reset code for a given email address
     AuthenticationResponseDTO verifyResetCode(String email, String code);

     // Confirms or denies a user's registration based on their email and confirmation status.
     String confirmUser(String email, Boolean confirm);

     // Registers a new user with pending comment data, allowing for initial feedback during the registration process.
     AuthenticationResponseDTO registerWithPendingComment(RegistrationRequestDTO registrationRequestDTO, Long sellerId, String comment,Integer grade);
}
