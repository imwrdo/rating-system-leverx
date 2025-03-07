package org.leverx.ratingapp.services.gameobject;

import org.leverx.ratingapp.dtos.gameobject.GameObjectRequestDTO;
import org.leverx.ratingapp.dtos.gameobject.GameObjectResponseDTO;

import java.util.List;

public interface GameObjectService {
     GameObjectResponseDTO create(GameObjectRequestDTO gameObject);
     List<GameObjectResponseDTO> getAll();
     GameObjectResponseDTO update(Long id, GameObjectRequestDTO gameObject);
     String delete(Long id);
}
