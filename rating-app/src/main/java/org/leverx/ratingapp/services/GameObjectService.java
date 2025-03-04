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

@Service
@AllArgsConstructor
public class GameObjectService {
    private GameObjectRepository gameObjectRepository;

    public GameObject create(GameObjectDTO gameObject) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            currentUser = (User) authentication.getPrincipal();
        }
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
}
