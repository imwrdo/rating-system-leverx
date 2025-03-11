package org.leverx.ratingapp.services.gameobject;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.gameobject.GameObjectRequestDTO;
import org.leverx.ratingapp.dtos.gameobject.GameObjectResponseDTO;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.repositories.GameObjectRepository;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationServiceImplementation;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
@Transactional
public class GameObjectServiceImplementation implements GameObjectService {
    private GameObjectRepository gameObjectRepository;
    private AuthenticationAndRegistrationServiceImplementation authAndRegService;

    @Override
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
                .updatedAt(game.getUpdatedAt())
                .status("Created")
                .build();
    }

    @Override
    public List<GameObjectResponseDTO> getAll() {
        List<GameObject> gameObjects = gameObjectRepository.findAll();
        return GameObjectResponseDTO.mapToGameObjectResponseDTO(gameObjects);
    }

    @Override
    public GameObjectResponseDTO update(Long id, GameObjectRequestDTO gameObject) {
        User currentUser = authAndRegService.getCurrentUser();
        GameObject gameObjectOriginal = gameObjectRepository.findById(id)
                .map(existingGame -> {
                    authAndRegService.authorizeUser(existingGame, currentUser);
                    existingGame.setTitle(gameObject.title());
                    existingGame.setText(gameObject.text());
                    existingGame.setUpdatedAt(LocalDateTime.now());
                    gameObjectRepository.save(existingGame);
                    return existingGame;
                })
                .orElseThrow(() -> new RuntimeException("Game object not found"));
        return GameObjectResponseDTO.builder()
                .id(gameObjectOriginal.getId())
                .title(gameObject.title())
                .text(gameObject.text())
                .userEmail(gameObjectOriginal.getUser().getEmail())
                .updatedAt(gameObjectOriginal.getUpdatedAt())
                .status("Updated")
                .build();
    }

    @Override
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
