package org.leverx.ratingapp.services.gameobject;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.gameobject.GameObjectRequestDTO;
import org.leverx.ratingapp.dtos.gameobject.GameObjectResponseDTO;
import org.leverx.ratingapp.models.entities.GameObject;
import org.leverx.ratingapp.models.entities.User;
import org.leverx.ratingapp.models.enums.Status;
import org.leverx.ratingapp.repositories.GameObjectRepository;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationServiceImplementation;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation of {@link GameObjectService} for managing game objects.
 * Provides CRUD operations such as creating, updating, retrieving, and deleting game objects.
 */
@Service
@AllArgsConstructor
@Transactional
public class GameObjectServiceImplementation implements GameObjectService {
    private GameObjectRepository gameObjectRepository;
    private AuthenticationAndRegistrationServiceImplementation authAndRegService;

    /**
     * Creates a new game object.
     *
     * @param gameObject The data transfer object (DTO) containing information about the game object to be created.
     * @return A {@link GameObjectResponseDTO} containing details of the created game object.
     * @throws RuntimeException If the game object cannot be created.
     */
    @Transactional
    @Override
    public GameObjectResponseDTO create(GameObjectRequestDTO gameObject) {
        // Get the current authenticated user
        User currentUser = authAndRegService.getCurrentUser();

        // Create a new GameObject instance
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

    /**
     * Retrieves all game objects from the repository.
     *
     * @return A list of {@link GameObjectResponseDTO} representing all the game objects.
     */
    @Override
    public List<GameObjectResponseDTO> getAll() {
        List<GameObject> gameObjects = gameObjectRepository.findAll();
        return GameObjectResponseDTO.mapToGameObjectResponseDTO(gameObjects);
    }

    /**
     * Updates an existing game object.
     *
     * @param id The ID of the game object to update.
     * @param gameObject The DTO containing updated data for the game object.
     * @return A {@link GameObjectResponseDTO} containing details of the updated game object.
     * @throws RuntimeException If the game object is not found or cannot be updated.
     */
    @Transactional
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
                .status(Status.UPDATED.getValueOfStatus())
                .build();
    }

    /**
     * Deletes an existing game object by ID.
     *
     * @param id The ID of the game object to delete.
     * @return The status of the operation (DELETED).
     * @throws RuntimeException If the game object is not found or cannot be deleted.
     */
    @Transactional
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
        return Status.DELETED.getValueOfStatus();
    }
}
