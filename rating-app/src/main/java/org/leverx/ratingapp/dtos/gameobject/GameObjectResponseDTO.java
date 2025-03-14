package org.leverx.ratingapp.dtos.gameobject;

import lombok.Builder;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record GameObjectResponseDTO(Long id, String title, String text, String userEmail, LocalDateTime updatedAt, String status) {

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
