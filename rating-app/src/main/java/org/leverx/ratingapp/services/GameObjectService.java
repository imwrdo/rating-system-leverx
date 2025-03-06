package org.leverx.ratingapp.services;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.GameObjectDTO;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.repositories.GameObjectRepository;
import org.leverx.ratingapp.services.auth.AuthenticationAndRegistrationService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class GameObjectService {
    private GameObjectRepository gameObjectRepository;
    private AuthenticationAndRegistrationService authAndRegService;



    public GameObject create(GameObjectDTO gameObject) {
        User currentUser = authAndRegService.getCurrentUser();

        var game = GameObject.builder()
                .title(gameObject.getTitle())
                .text(gameObject.getText())
                .user(currentUser)
                .build();
        gameObjectRepository.save(game);

        return game;
    }

    public List<GameObject> getAll() {
        return gameObjectRepository.findAll();
    }

    public GameObject update(Long id, GameObjectDTO gameObject) {
        User currentUser = authAndRegService.getCurrentUser();

        return gameObjectRepository.findById(id)
                .map(existingGame -> {
                    authAndRegService.authorizeUser(existingGame, currentUser);
                    existingGame.setTitle(gameObject.getTitle());
                    existingGame.setText(gameObject.getText());
                    gameObjectRepository.save(existingGame);
                    return existingGame;
                })
                .orElseThrow(() -> new RuntimeException("Game object not found"));
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
