package org.leverx.ratingapp.service;

import org.leverx.ratingapp.repository.ConfirmationTokenRepository;
import org.leverx.ratingapp.entity.token.ConfirmationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository
                .save(confirmationToken);
    }

    public Optional<ConfirmationToken> getConfirmationToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public void setConfirmationDate(String token) {
        confirmationTokenRepository
                .updateConfirmedAt(
                  token,LocalDateTime.now()
                );
    }

}
