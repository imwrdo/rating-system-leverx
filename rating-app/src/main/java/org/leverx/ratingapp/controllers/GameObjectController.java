package org.leverx.ratingapp.controllers;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.GameObjectDTO;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.services.GameObjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path ="object")
public class GameObjectController {
    private final GameObjectService service;

    @PostMapping
    public ResponseEntity<GameObject> create(
            @RequestBody GameObjectDTO gameObject) {
        return ResponseEntity.ok(service.create(gameObject));
    }

    @GetMapping
    public ResponseEntity<List<GameObject>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<GameObject> update(@PathVariable Long id,
                                             @RequestBody GameObjectDTO gameObject) {
        return ResponseEntity.ok(service.update(id,gameObject));
    }
}
