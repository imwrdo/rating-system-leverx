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
import org.leverx.ratingapp.services.rating.RatingCalculationServiceImplementation;
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
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final GameObjectRepository gameObjectRepository;
    private final RatingCalculationServiceImplementation ratingCalculationServiceImplementation;

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
                
        List<Comment> comments = commentRepository.findAll();
        List<GameObject> gameObjects = gameObjectRepository.findAll();

        return users.stream()
            .map(user -> {
                Double rating = ratingCalculationServiceImplementation.getSellerRating(user.getId());
                Integer totalRatings = ratingCalculationServiceImplementation.getNumberOfRatings(user.getId());
                return UserDTO.mapToUserDTO(user, comments, gameObjects, isAdmin, rating, totalRatings);
            })
            .collect(Collectors.toList());
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
                                    
        List<Comment> comments = commentRepository.findAll();
        List<GameObject> gameObjects = gameObjectRepository.findAll();
        
        Double rating = ratingCalculationServiceImplementation.getSellerRating(user_id);
        Integer totalRatings = ratingCalculationServiceImplementation.getNumberOfRatings(user_id);
        
        return UserDTO.mapToUserDTO(
                user,
                comments,
                gameObjects,
                !onlyActive,
                rating,
                totalRatings
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

        Map<Long, Double> userRatings = users.stream()
                .collect(Collectors.toMap(
                    User::getId,
                    user -> ratingCalculationServiceImplementation.getSellerRating(user.getId())
                ));

        Map<Long, Integer> userTotalRatings = users.stream()
                .collect(Collectors.toMap(
                    User::getId,
                    user -> ratingCalculationServiceImplementation.getNumberOfRatings(user.getId())
                ));

        return users.stream()
                .sorted((u1, u2) -> {
                    int ratingCompare = Double.compare(
                            userRatings.getOrDefault(u2.getId(), 0.0),
                            userRatings.getOrDefault(u1.getId(), 0.0)
                    );
                    if (ratingCompare != 0) {
                        return ratingCompare;
                    }
                    return Integer.compare(
                            userTotalRatings.getOrDefault(u2.getId(), 0),
                            userTotalRatings.getOrDefault(u1.getId(), 0)
                    );
                })
                .limit(ratingLimit != null && ratingLimit > 0 ? ratingLimit : Long.MAX_VALUE)
                .map(user -> UserRankingDTO.builder()
                        .place((long) (users.indexOf(user) + 1))
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .createdAt(user.getCreatedAt())
                        .rating(userRatings.get(user.getId()))
                        .totalCommentNumber(userTotalRatings.get(user.getId()))
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
