package org.leverx.ratingapp.dtos.gameobject;

import lombok.Builder;
import org.leverx.ratingapp.entities.GameObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record GameObjectResponseDTO(Long id, String title, String text, String userEmail, LocalDateTime updated_at, String status) {

    public static List<GameObjectResponseDTO> mapToGameObjectResponseDTO(List<GameObject> gameObjects){
        return gameObjects.stream()
                .map(gameObject -> GameObjectResponseDTO.builder()
                        .id(gameObject.getId())
                        .title(gameObject.getTitle())
                        .text(gameObject.getText())
                        .userEmail(gameObject.getUser().getEmail())
                        .updated_at(gameObject.getUpdated_at())
                        .status("Active")
                        .build())
                .collect(Collectors.toList());
    }
}
