package org.leverx.ratingapp.services.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.user.UserDTO;
import org.leverx.ratingapp.dtos.user.UserRankingDTO;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String
                                .format("User with email %s not found", email)));
    }


    @Override
    public void enableUser(String email) {
        userRepository.enableUser(email);
    }

    @Override
    public List<UserDTO> getAllUsers(boolean onlyActive, boolean isAdmin) {
        List<User> users = onlyActive
                ? userRepository.findAllActiveUsers()
                : userRepository.findAll();
        return mapToUsersDTO(users, isAdmin);
    }

    @Override
    public UserDTO getUserById(Long user_id, boolean onlyActive) {
        User user = onlyActive
                ? userRepository.findActiveUserById(user_id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Active user with id %s not found"
                                    .formatted(user_id)))
                : userRepository.findById(user_id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Active user with id %s not found"
                                    .formatted(user_id)));
        return UserDTO.mapToUserDTO(
                user,
                commentRepository.findAll(),
                gameObjectRepository.findAll(),
                !onlyActive
        );
    }

    @Override
    public List<UserDTO> getInactiveUsers() {
        List<User> users = userRepository.findAllInactiveUsers();
        return mapToUsersDTO(users,true);
    }

    @Override
    public List<UserDTO> getPendingUsers() {
        List<User> users = userRepository.findPendingUsers();
        return mapToUsersDTO(users, true);
    }

    @Override
    public List<UserRankingDTO> getUserRating(String gameName, Long ratingLimit) {
        List<User> users = userRepository.findAllActiveUsers();
        List<Comment> comments = commentRepository.findAll();

        if (gameName != null && !gameName.isEmpty()) {
            List<Long> relatedGameIds = gameObjectRepository
                    .findAllByTitleContainingIgnoreCase(gameName)
                    .stream()
                    .map(GameObject::getId)
                    .toList();
            comments = comments.stream()
                    .filter(comment -> relatedGameIds.contains(comment.getSeller().getId()))
                    .toList();
        }

        comments = comments.stream()
                .filter(Comment::getIsApproved)
                .toList();

        Map<Long, Long> userCommentCount = comments.stream()
                .collect(Collectors.groupingBy(comment ->
                                comment.getSeller().getId(),
                        Collectors.counting()));


        return users.stream()
                .sorted((u1, u2) -> Long.compare(
                        userCommentCount.getOrDefault(u2.getId(), 0L),
                        userCommentCount.getOrDefault(u1.getId(), 0L))
                )
                .limit(ratingLimit != null && ratingLimit > 0 ? ratingLimit : Long.MAX_VALUE)
                .map(user -> UserRankingDTO.builder()
                        .place((long) (users.indexOf(user) + 1))
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .createdAt(user.getCreatedAt())
                        .commentCount(userCommentCount.getOrDefault(user.getId(), 0L).intValue())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private List<UserDTO> mapToUsersDTO(List<User> users, Boolean isAdmin) {
        List<Comment> comments = commentRepository.findAll();
        List<GameObject> games = gameObjectRepository.findAll();
        return UserDTO.mapToUsersDTO(users, comments, games,isAdmin);
    }

}
