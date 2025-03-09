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
        String first_name,
        String last_name,
        String email,
        LocalDateTime created_at,
        Role role,
        List<CommentResponseDTO> comments,
        List<GameObjectResponseDTO> gameObjects) {


    public static UserDTO mapToUserDTO(User user, List<Comment> comments, List<GameObject> gameObjects) {
        List<Comment> userComments = comments.stream()
                .filter(comment -> comment.getAuthor().getId().equals(user.getId()))
                .toList();
        List<GameObject> userGameObjects =  gameObjects.stream()
                .filter(gameObject -> gameObject.getUser().getId().equals(user.getId()))
                .toList();
        return UserDTO.builder()
                .id(user.getId())
                .first_name(user.getFirst_name())
                .last_name(user.getLast_name())
                .email(user.getEmail())
                .created_at(user.getCreated_at())
                .role(user.getRole())
                .comments(CommentResponseDTO.mapToCommentResponseDTO(userComments))
                .gameObjects(GameObjectResponseDTO.mapToGameObjectResponseDTO(userGameObjects))
                .build();
    }

    public static List<UserDTO> mapToUsersDTO(List<User> users, List<Comment> comments, List<GameObject> gameObjects) {
        return users.stream()
                .map(user -> mapToUserDTO(
                        user,
                        comments,
                        gameObjects))
                .collect(Collectors.toList());
    }


}
