package org.leverx.ratingapp.service;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.entity.User;
import org.leverx.ratingapp.repository.UserRepository;
import org.leverx.ratingapp.entity.token.ConfirmationToken;
import org.leverx.ratingapp.service.auth.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    public void setUserRepository(UserRepository userRepository,
                                  BCryptPasswordEncoder passwordEncoder,
                                  ConfirmationTokenService confirmationTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(()->
                        new UsernameNotFoundException(String
                                .format(USER_NOT_FOUND_MSG,email)));
    }


    public void enableUser(String email) {
        userRepository.enableUser(email);
    }
}
