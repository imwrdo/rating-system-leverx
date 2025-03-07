package org.leverx.ratingapp.services;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.gameobject.GameObjectRequestDTO;
import org.leverx.ratingapp.dtos.gameobject.GameObjectResponseDTO;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.repositories.GameObjectRepository;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
public class GameObjectService {
    private GameObjectRepository gameObjectRepository;
    private AuthenticationAndRegistrationService authAndRegService;



    public GameObjectResponseDTO create(GameObjectRequestDTO gameObject) {
        User currentUser = authAndRegService.getCurrentUser();

        var game = GameObject.builder()
                .title(gameObject.title())
                .text(gameObject.text())
                .user(currentUser)
                .build();
        gameObjectRepository.save(game);

        return GameObjectResponseDTO.builder()
                .id(game.getId())
                .title(gameObject.title())
                .text(gameObject.text())
                .userEmail(game.getUser().getEmail())
                .updated_at(game.getUpdated_at())
                .status("Created")
                .build();
    }

    public List<GameObjectResponseDTO> getAll() {
        List<GameObject> gameObjects = gameObjectRepository.findAll();
        return GameObjectResponseDTO.mapToGameObjectResponseDTO(gameObjects);
    }

    public GameObjectResponseDTO update(Long id, GameObjectRequestDTO gameObject) {
        User currentUser = authAndRegService.getCurrentUser();
        GameObject gameObjectOriginal = gameObjectRepository.findById(id)
                .map(existingGame -> {
                    authAndRegService.authorizeUser(existingGame, currentUser);
                    existingGame.setTitle(gameObject.title());
                    existingGame.setText(gameObject.text());
                    existingGame.setUpdated_at(LocalDateTime.now());
                    gameObjectRepository.save(existingGame);
                    return existingGame;
                })
                .orElseThrow(() -> new RuntimeException("Game object not found"));
        return GameObjectResponseDTO.builder()
                .id(gameObjectOriginal.getId())
                .title(gameObject.title())
                .text(gameObject.text())
                .userEmail(gameObjectOriginal.getUser().getEmail())
                .updated_at(gameObjectOriginal.getUpdated_at())
                .status("Updated")
                .build();
    }

    public String delete(Long id) {
        User currentUser = authAndRegService.getCurrentUser();

        gameObjectRepository.findById(id)
                .map(existingGame -> {
                    authAndRegService.authorizeUser(existingGame, currentUser);
                    gameObjectRepository.delete(existingGame);
                    return existingGame;
                })
                .orElseThrow(() -> new RuntimeException("Game object not found"));
        return "Game object deleted";
    }
}
