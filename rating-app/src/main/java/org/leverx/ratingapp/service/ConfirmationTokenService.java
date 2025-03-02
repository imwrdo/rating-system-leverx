package org.leverx.ratingapp.service;

import org.leverx.ratingapp.repository.ConfirmationTokenRepository;
import org.leverx.ratingapp.entity.token.ConfirmationToken;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

}
