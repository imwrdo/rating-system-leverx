package org.leverx.ratingapp.services.auth;

import org.leverx.ratingapp.repositories.ConfirmationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public void saveConfirmationToken(String email, String token) {
        confirmationTokenRepository.saveToken(email, token);
    }

    public Optional<String> getConfirmationToken(String email) {
        return Optional.ofNullable(confirmationTokenRepository.getToken(email));
    }

    public void removeConfirmationToken(String email) {
        confirmationTokenRepository.removeToken(email);
    }
}
