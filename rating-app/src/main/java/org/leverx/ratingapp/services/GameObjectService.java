package org.leverx.ratingapp.services;

import lombok.AllArgsConstructor;
import org.leverx.ratingapp.dtos.GameObjectDTO;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.repositories.GameObjectRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GameObjectService {
    private GameObjectRepository gameObjectRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
                .filter(auth -> auth.getPrincipal() instanceof UserDetails)
                .map(auth -> (User) auth.getPrincipal())
                .orElseThrow(() -> new RuntimeException("Invalid authentication"));
    }
    private void authorizeUser(GameObject gameObject, User currentUser) {
        if (!gameObject.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to perform this action");
        }
    }


    public GameObject create(GameObjectDTO gameObject) {
        User currentUser = getCurrentUser();

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
        User currentUser = getCurrentUser();

        return gameObjectRepository.findById(id)
                .map(existingGame -> {
                    authorizeUser(existingGame, currentUser);
                    existingGame.setTitle(gameObject.getTitle());
                    existingGame.setText(gameObject.getText());
                    gameObjectRepository.save(existingGame);
                    return existingGame;
                })
                .orElseThrow(() -> new RuntimeException("Game object not found"));
    }

    public String delete(Long id) {
        User currentUser = getCurrentUser();

        gameObjectRepository.findById(id)
                .map(existingGame -> {
                    authorizeUser(existingGame, currentUser);
                    gameObjectRepository.delete(existingGame);
                    return existingGame;
                })
                .orElseThrow(() -> new RuntimeException("Game object not found"));
        return "Game object deleted";
    }
}
