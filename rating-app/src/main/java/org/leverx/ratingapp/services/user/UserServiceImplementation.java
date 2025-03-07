package org.leverx.ratingapp.services.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Transactional
@AllArgsConstructor
public class UserServiceImplementation implements UserDetailsService, UserService {
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(()->
                        new UsernameNotFoundException(String
                                .format("User with email %s not found",email)));
    }


    @Override
    public void enableUser(String email) {
        userRepository.enableUser(email);
    }
}
