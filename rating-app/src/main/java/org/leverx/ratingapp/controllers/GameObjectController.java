package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.gameobject.GameObjectRequestDTO;
import org.leverx.ratingapp.dtos.gameobject.GameObjectResponseDTO;
import org.leverx.ratingapp.services.gameobject.GameObjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * GameObjectController is a REST controller that manages game objects in the application.
 * It provides endpoints to create, retrieve, update, and delete game objects.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path ="object")
public class GameObjectController {
    private final GameObjectService service; // Service for handling game object operations

    /**
     * Endpoint to create a new game object.
     *
     * @param gameObject the details of the game object to create
     * @return a {@link ResponseEntity} containing the created game object as a {@link GameObjectResponseDTO} object
     */
    @PostMapping
    public ResponseEntity<GameObjectResponseDTO> create(
            @RequestBody GameObjectRequestDTO gameObject) {
        GameObjectResponseDTO response = service.create(gameObject);
        return ResponseEntity.created(
                URI.create(String.format("/object/%d", response.id()))
                ).body(response);
    }

    /**
     * Endpoint to retrieve all game objects.
     *
     * @return a {@link ResponseEntity} containing a list of all game objects as {@link GameObjectResponseDTO} objects
     */
    @GetMapping
    public ResponseEntity<List<GameObjectResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /**
     * Endpoint to update an existing game object.
     *
     * @param game_object_id the ID of the game object to update
     * @param gameObject the updated game object details
     * @return a {@link ResponseEntity} containing the updated game object as a {@link GameObjectResponseDTO} object
     */
    @PutMapping(path = "{game_object_id}")
    public ResponseEntity<GameObjectResponseDTO> update(
            @PathVariable Long game_object_id,
            @RequestBody GameObjectRequestDTO gameObject) {
        return ResponseEntity.ok(service.update(game_object_id,gameObject));
    }

    /**
     * Endpoint to delete a game object by its ID.
     *
     * @param game_object_id the ID of the game object to delete
     * @return a {@link ResponseEntity} containing a status message after deletion
     */
    @DeleteMapping("{game_object_id}")
    public ResponseEntity<String> delete(@PathVariable Long game_object_id) {
        return ResponseEntity.status(202).body(service.delete(game_object_id));
    }
}
