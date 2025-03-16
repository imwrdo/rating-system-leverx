package org.leverx.ratingapp.dtos.gameobject;

import lombok.Builder;
import org.leverx.ratingapp.models.entities.GameObject;
import org.leverx.ratingapp.models.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
/**
 * DTO representing a response containing details about a game object.
 */
 @Builder
public record GameObjectResponseDTO(Long id, String title, String text, String userEmail, LocalDateTime updatedAt, String status) {
    /**
     * Converts a list of {@link GameObject} entities to a list of GameObjectResponseDTOs.
     *
     * @param gameObjects A list of {@link GameObject} entities to be converted.
     * @return A list of GameObjectResponseDTOs.
     */
    public static List<GameObjectResponseDTO> mapToGameObjectResponseDTO(List<GameObject> gameObjects){
        return gameObjects.stream()
                .map(gameObject -> GameObjectResponseDTO.builder()
                        .id(gameObject.getId())
                        .title(gameObject.getTitle())
                        .text(gameObject.getText())
                        .userEmail(gameObject.getUser().getEmail())
                        .updatedAt(gameObject.getUpdatedAt())
                        .status(Status.ACTIVE.getValueOfStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
