package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.gameobject.GameObjectRequestDTO;
import org.leverx.ratingapp.dtos.gameobject.GameObjectResponseDTO;
import org.leverx.ratingapp.services.gameobject.GameObjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path ="object")
public class GameObjectController {
    private final GameObjectService service;

    @PostMapping
    public ResponseEntity<GameObjectResponseDTO> create(
            @RequestBody GameObjectRequestDTO gameObject) {
        return ResponseEntity.ok(service.create(gameObject));
    }

    @GetMapping
    public ResponseEntity<List<GameObjectResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<GameObjectResponseDTO> update(@PathVariable Long id,
                                             @RequestBody GameObjectRequestDTO gameObject) {
        return ResponseEntity.ok(service.update(id,gameObject));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
