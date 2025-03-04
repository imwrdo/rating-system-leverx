package org.leverx.ratingapp.repositories;

import org.leverx.ratingapp.entities.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameObjectRepository extends JpaRepository<GameObject, Long> {

}
