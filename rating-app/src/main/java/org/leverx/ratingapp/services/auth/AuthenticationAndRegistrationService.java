package org.leverx.ratingapp.services.auth;

import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.auth.AuthenticationResponseDTO;
import org.leverx.ratingapp.dtos.auth.RegistrationRequestDTO;
import org.leverx.ratingapp.entities.User;

public interface AuthenticationAndRegistrationService {
     User getCurrentUser();
     <T> void authorizeUser(T entity, User currentUser);
     AuthenticationResponseDTO register(RegistrationRequestDTO registrationRequestDTO);
     AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
     String confirmToken(String token);
     String buildEmail(String name, String link);
}
