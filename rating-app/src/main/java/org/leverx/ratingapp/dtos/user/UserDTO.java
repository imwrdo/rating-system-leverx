package org.leverx.ratingapp.dtos.user;

import lombok.Builder;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.dtos.gameobject.GameObjectResponseDTO;
import org.leverx.ratingapp.models.entities.Comment;
import org.leverx.ratingapp.models.entities.GameObject;
import org.leverx.ratingapp.models.entities.User;
import org.leverx.ratingapp.models.enums.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO representing a {@link User} with their details, including personal information,
 * rating, and associated comments and game objects.
 */
 @Builder
public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt,
        Role role,
        Double rating,
        Integer totalRatings,
        List<CommentResponseDTO> comments,
        List<GameObjectResponseDTO> gameObjects) {

    /**
     * Maps a {@link User} entity to a UserDTO with associated {@link Comment (s)} and {@link GameObject (s)}.
     *
     * @param user           The {@link User} entity to be mapped.
     * @param comments       The list of all {@link Comment}.
     * @param gameObjects    The list of all {@link GameObject}.
     * @param isAdmin        Flag to indicate if the current user is an admin.
     * @param rating         The rating for the user, usually calculated in the service layer.
     * @param totalRatings   The total number of ratings the user has received.
     * @return A mapped UserDTO object.
     */
    public static UserDTO mapToUserDTO(User user, List<Comment> comments, List<GameObject> gameObjects, Boolean isAdmin, Double rating, Integer totalRatings) {
        // Filter comments based on whether the user is an admin or not
        List<Comment> userComments = comments.stream()
                .filter(comment ->isAdmin
                        ? comment.getSeller().getId()
                                .equals(user.getId())
                        : comment.getSeller().getId()
                                .equals(user.getId())
                            && comment.getIsApproved())
                .toList();
        // Filter game objects associated with the user
        List<GameObject> userGameObjects =  gameObjects.stream()
                .filter(gameObject -> gameObject.getUser().getId().equals(user.getId()))
                .toList();
        // Return the mapped UserDTO
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .rating(rating)
                .totalRatings(totalRatings)
                .comments(CommentResponseDTO.mapToCommentResponseDTO(userComments))
                .gameObjects(GameObjectResponseDTO.mapToGameObjectResponseDTO(userGameObjects))
                .build();
    }

    /**
     * Maps a list of {@link User} entities to a list of UserDTOs with associated {@link Comment (s)} and {@link GameObject (s)}.
     *
     * @param users         The list of user entities to be mapped.
     * @param comments      The list of all {@link Comment}.
     * @param gameObjects   The list of all {@link GameObject}.
     * @param isAdmin       Flag to indicate if the current user is an admin.
     * @return A list of mapped UserDTOs.
     */
    public static List<UserDTO> mapToUsersDTO(List<User> users, List<Comment> comments, List<GameObject> gameObjects, Boolean isAdmin) {
        return users.stream()
                .map(user -> mapToUserDTO(
                        user,
                        comments,
                        gameObjects,
                        isAdmin,
                        0.0, // Rating is typically calculated in the service layer.
                        0     // Total ratings are also calculated in the service layer.
                ))
                .collect(Collectors.toList());
    }


}
