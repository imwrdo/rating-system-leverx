package org.leverx.ratingapp.services.gameobject;

import org.leverx.ratingapp.dtos.gameobject.GameObjectRequestDTO;
import org.leverx.ratingapp.dtos.gameobject.GameObjectResponseDTO;

import java.util.List;

/**
 * Provides CRUD operations such as creating, updating, retrieving, and deleting game objects
 */
public interface GameObjectService {
     // Creates a new game object
     GameObjectResponseDTO create(GameObjectRequestDTO gameObject);

     // Retrieves all game objects from the repository.
     List<GameObjectResponseDTO> getAll();

     // Updates an existing game object
     GameObjectResponseDTO update(Long id, GameObjectRequestDTO gameObject);

     // Deletes an existing game object by ID.
     String delete(Long id);
}
