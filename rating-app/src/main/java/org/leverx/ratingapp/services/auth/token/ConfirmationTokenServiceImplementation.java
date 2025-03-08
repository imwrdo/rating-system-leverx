package org.leverx.ratingapp.services.auth.token;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.repositories.token.ConfirmationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class ConfirmationTokenServiceImplementation implements ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public void saveConfirmationToken(String email, String token) {
        confirmationTokenRepository.saveToken(email, token);
    }

    @Override
    public Optional<String> getConfirmationToken(String email) {
        return Optional.ofNullable(confirmationTokenRepository.getToken(email));
    }

    @Override
    public void removeConfirmationToken(String email) {
        confirmationTokenRepository.removeToken(email);
    }

    @Override
    public void saveResetCode(String email, String resetCode) {
        confirmationTokenRepository.saveResetCode(email, resetCode);
    }

    @Override
    public String getResetCode(String email) {
        return confirmationTokenRepository.getResetCode(email);
    }

    @Override
    public void removeResetCode(String email) {
        confirmationTokenRepository.removeResetCode(email);
    }


}
