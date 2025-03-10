package org.leverx.ratingapp.services.auth;

import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.auth.AuthenticationResponseDTO;
import org.leverx.ratingapp.dtos.auth.PasswordResetRequestDTO;
import org.leverx.ratingapp.dtos.auth.registration.RegistrationRequestDTO;
import org.leverx.ratingapp.entities.User;

public interface AuthenticationAndRegistrationService {
     User getCurrentUser();
     <T> void authorizeUser(T entity, User currentUser);
     AuthenticationResponseDTO register(RegistrationRequestDTO registrationRequestDTO);
     AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
     String confirmToken(String token);
     AuthenticationResponseDTO initiatePasswordReset(String email);
     AuthenticationResponseDTO resetPassword(PasswordResetRequestDTO request);
     AuthenticationResponseDTO verifyResetCode(String email, String code);
     String confirmUser(String email, Boolean confirm);
}
