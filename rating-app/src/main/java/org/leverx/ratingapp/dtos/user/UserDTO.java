package org.leverx.ratingapp.dtos.user;

import lombok.Builder;
import org.leverx.ratingapp.dtos.comments.CommentResponseDTO;
import org.leverx.ratingapp.dtos.gameobject.GameObjectResponseDTO;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.enums.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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


    public static UserDTO mapToUserDTO(User user, List<Comment> comments, List<GameObject> gameObjects, Boolean isAdmin, Double rating, Integer totalRatings) {
        List<Comment> userComments = comments.stream()
                .filter(comment ->isAdmin
                        ? comment.getSeller().getId()
                                .equals(user.getId())
                        : comment.getSeller().getId()
                                .equals(user.getId())
                            && comment.getIsApproved())
                .toList();
        List<GameObject> userGameObjects =  gameObjects.stream()
                .filter(gameObject -> gameObject.getUser().getId().equals(user.getId()))
                .toList();
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

    public static List<UserDTO> mapToUsersDTO(List<User> users, List<Comment> comments, List<GameObject> gameObjects, Boolean isAdmin) {
        return users.stream()
                .map(user -> mapToUserDTO(
                        user,
                        comments,
                        gameObjects,
                        isAdmin,
                        0.0,  // The rating will be set by service layer
                        0     // The total ratings will be set by service layer
                ))
                .collect(Collectors.toList());
    }


}
