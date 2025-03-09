package org.leverx.ratingapp.services.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.user.UserRankingDTO;
import org.leverx.ratingapp.dtos.user.UserDTO;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.exceptions.ResourceNotFoundException;
import org.leverx.ratingapp.repositories.CommentRepository;
import org.leverx.ratingapp.repositories.GameObjectRepository;
import org.leverx.ratingapp.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
        List<User> users = userRepository.findAllIsActivated();
        List<Comment> comments = commentRepository.findAll();
        List<GameObject> games = gameObjectRepository.findAll();
        return UserDTO.mapToUsersDTO(users,comments,games);
    }

    @Override
    public UserDTO getActiveUser(Long sellerId) {
        User user = userRepository.findIsActivatedById(sellerId)
                .orElseThrow(()->
                        new ResourceNotFoundException(String
                                .format("User with id %s not found",sellerId)));
        return UserDTO.mapToUserDTO(user,commentRepository.findAll(),gameObjectRepository.findAll());
    }

    @Override
    public List<UserRankingDTO> getUserRanking() {
        List<User> users = userRepository.findAllIsActivated();
        List<Comment> comments = commentRepository.findAll();

        Map<Long, Long> userCommentCount = comments.stream()
                .collect(Collectors.groupingBy(comment ->
                        comment.getAuthor().getId(),
                        Collectors.counting()));

        List<User> sortedUsers = users.stream()
                .sorted((u1, u2) -> Long.compare(
                        userCommentCount.getOrDefault(u2.getId(), 0L),
                        userCommentCount.getOrDefault(u1.getId(), 0L))
                )
                .toList();

        return sortedUsers.stream()
                .map(user -> UserRankingDTO.builder()
                        .place(sortedUsers.indexOf(user) + 1)
                        .id(user.getId())
                        .first_name(user.getFirst_name())
                        .last_name(user.getLast_name())
                        .email(user.getEmail())
                        .created_at(user.getCreated_at())
                        .commentCount(userCommentCount.getOrDefault(user.getId(), 0L).intValue())
                        .build()
                )
                .limit(10)
                .collect(Collectors.toList());
    }

}
