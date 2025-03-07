package org.leverx.ratingapp.services.auth.token;


import java.util.Optional;

public interface ConfirmationTokenService {
     void saveConfirmationToken(String email, String token);
     Optional<String> getConfirmationToken(String email);
     void removeConfirmationToken(String email);

}
