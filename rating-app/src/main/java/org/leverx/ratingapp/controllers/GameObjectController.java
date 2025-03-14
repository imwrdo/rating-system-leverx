package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.gameobject.GameObjectRequestDTO;
import org.leverx.ratingapp.dtos.gameobject.GameObjectResponseDTO;
import org.leverx.ratingapp.services.gameobject.GameObjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path ="object")
public class GameObjectController {
    private final GameObjectService service;

    @PostMapping
    public ResponseEntity<GameObjectResponseDTO> create(
            @RequestBody GameObjectRequestDTO gameObject) {
        GameObjectResponseDTO response = service.create(gameObject);
        return ResponseEntity.created(
                URI.create(String.format("/object/%d", response.id()))
        ).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GameObjectResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping(path = "{game_object_id}")
    public ResponseEntity<GameObjectResponseDTO> update(@PathVariable Long game_object_id,
                                             @RequestBody GameObjectRequestDTO gameObject) {
        return ResponseEntity.ok(service.update(game_object_id,gameObject));
    }

    @DeleteMapping("{game_object_id}")
    public ResponseEntity<String> delete(@PathVariable Long game_object_id) {

        return ResponseEntity.status(202).body(service.delete(game_object_id));
    }
}
