package org.leverx.ratingapp.repositories;

import jakarta.transaction.Transactional;
import org.leverx.ratingapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.is_activated = TRUE WHERE a.email = ?1")
    void enableUser(String email);

    @Query("SELECT u FROM User u WHERE u.is_activated = true")
    List<User> findAllIsActivated();

    @Query("SELECT u FROM User u WHERE u.is_activated = true AND u.id = ?1")
    Optional<User> findIsActivatedById(Long id);
}
