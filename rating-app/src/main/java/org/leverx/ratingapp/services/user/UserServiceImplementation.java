package org.leverx.ratingapp.services.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.user.UserDTO;
import org.leverx.ratingapp.dtos.user.UserRankingDTO;
import org.leverx.ratingapp.models.entities.Comment;
import org.leverx.ratingapp.models.entities.GameObject;
import org.leverx.ratingapp.models.entities.User;
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

/**
 * Service implementation of {@link UserService} for managing user details,
 * including user retrieval, enabling/disabling users, user ratings, and user rankings.
 * Implements methods for working with user data and calculating their ratings.
 */
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImplementation implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final GameObjectRepository gameObjectRepository;
    private final RatingCalculationServiceImplementation ratingCalculationServiceImplementation;

    /**
     * Loads user details by email. Used for user authentication.
     *
     * @param email The email of the user to load.
     * @return The {@link UserDetails} for the specified email.
     * @throws UsernameNotFoundException If no user with the specified email is found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String
                                .format("User with email %s not found", email)));
    }

    /**
     * Enables a user by their email address.
     *
     * @param email The email of the user to be enabled.
     */
    @Override
    public void enableUser(String email) {
        userRepository.enableUser(email);
    }

    /**
     * Retrieves a list of all users. Filters users based on their active status.
     *
     * @param onlyActive Whether to return only active users.
     * @param isAdmin Indicates whether the caller is an admin (affects the data returned).
     * @return A list of {@link UserDTO} representing the users.
     */
    @Override
    public List<UserDTO> getAllUsers(boolean onlyActive, boolean isAdmin) {
        // Fetch all users based on the active status
        List<User> users = onlyActive
                ? userRepository.findAllActiveUsers()
                : userRepository.findAll();

        // Retrieve all comments and game objects for rating calculations
        List<Comment> comments = commentRepository.findAll();
        List<GameObject> gameObjects = gameObjectRepository.findAll();

        // Map each user to a UserDTO and calculate rating data
        return users.stream()
            .map(user -> createUserDTO(user, comments, gameObjects, isAdmin))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID, with an option to filter by active status.
     *
     * @param user_id The ID of the user to retrieve.
     * @param onlyActive Whether to retrieve only active users.
     * @return A {@link UserDTO} representing the retrieved user.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    public UserDTO getUserById(Long user_id, boolean onlyActive) {
        // Fetch the user based on their active status
        User user = onlyActive
                ? userRepository.findActiveUserById(user_id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Active user with id %s not found"
                                    .formatted(user_id)))
                : userRepository.findById(user_id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Active user with id %s not found"
                                    .formatted(user_id)));

        // Retrieve all comments and game objects for rating calculations
        List<Comment> comments = commentRepository.findAll();
        List<GameObject> gameObjects = gameObjectRepository.findAll();

        return createUserDTO(user, comments, gameObjects, !onlyActive);
    }

    /**
     * Retrieves a list of inactive users.
     *
     * @return A list of {@link UserDTO} representing inactive users.
     */
    @Override
    public List<UserDTO> getInactiveUsers() {
        List<User> users = userRepository.findAllInactiveUsers();
        return mapToUsersDTO(users,true);
    }

    /**
     * Retrieves a list of users whose registration is pending.
     *
     * @return A list of {@link UserDTO} representing pending users.
     */
    @Override
    public List<UserDTO> getPendingUsers() {
        List<User> users = userRepository.findPendingUsers();
        return mapToUsersDTO(users, true);
    }

    /**
     * Retrieves a list of user rankings, sorted by their rating. Optionally filters rankings by game name.
     *
     * @param gameName The name of the game to filter by (optional).
     * @param ratingLimit The maximum number of user rankings to retrieve.
     * @return A list of {@link UserRankingDTO} representing the rankings.
     */
    @Override
    public List<UserRankingDTO> getUserRating(String gameName, Long ratingLimit) {
        // Fetch all active users and comments
        List<User> users = userRepository.findAllActiveUsers();
        List<Comment> comments = commentRepository.findAll();

        // Filter comments related to the specified game name if provided
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
        // Filter only approved comments
        comments = comments.stream()
                .filter(Comment::getIsApproved)
                .toList();

        // Calculate user ratings and total ratings
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

        // Sort users by rating and total number of ratings
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

    /**
     * Helper method to map a list of users to a list of UserDTOs.
     *
     * @param users The list of users to map.
     * @param isAdmin Whether the caller is an admin (affects the data returned).
     * @return A list of {@link UserDTO}.
     */
    private List<UserDTO> mapToUsersDTO(List<User> users, Boolean isAdmin) {
        List<Comment> comments = commentRepository.findAll();
        List<GameObject> games = gameObjectRepository.findAll();
        return UserDTO.mapToUsersDTO(users, comments, games,isAdmin);
    }

    // Helper method to calculate user ratings and total comments
    private record UserRatingData(Double rating, Integer totalComments) {}
    
    private UserRatingData calculateUserRatingData(Long userId) {
        return new UserRatingData(
            ratingCalculationServiceImplementation.getSellerRating(userId),
            ratingCalculationServiceImplementation.getNumberOfRatings(userId)
        );
    }

    private UserDTO createUserDTO(User user, List<Comment> comments, List<GameObject> gameObjects, boolean isAdmin) {
        UserRatingData ratingData = calculateUserRatingData(user.getId());
        return UserDTO.mapToUserDTO(
            user, 
            comments, 
            gameObjects, 
            isAdmin, 
            ratingData.rating(), 
            ratingData.totalComments()
        );
    }

}
