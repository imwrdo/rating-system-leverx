package org.leverx.ratingapp.services.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.UserDTO;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.GameObjectRepository;
import org.leverx.ratingapp.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
@AllArgsConstructor
public class UserServiceImplementation implements UserDetailsService, UserService {
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private GameObjectRepository gameObjectRepository;
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

    @Override
    public List<UserDTO> getAllActivatedUsers() {
        List<User> user = userRepository.findAllIsActivated();
        List<Comment> comments = commentRepository.findAll();
        List<GameObject> games = gameObjectRepository.findAll();
        return UserDTO.mapToUserDTO(user,comments,games);
    }
}
