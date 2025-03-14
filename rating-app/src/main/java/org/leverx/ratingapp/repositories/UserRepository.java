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
            "SET a.isActivated = TRUE WHERE a.email = ?1")
    void enableUser(String email);

    @Query("SELECT u FROM User u WHERE u.isActivated = true AND u.role != 'ADMIN'")
    List<User> findAllActiveUsers();

    @Query("SELECT u FROM User u WHERE u.isActivated = true AND u.id = ?1")
    Optional<User> findActiveUserById(Long id);

    @Query("SELECT u FROM User u WHERE u.isEmailConfirmed = false AND u.isActivated = false")
    List<User> findAllInactiveUsers();

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.isActivated = true AND u.id = ?1")
    boolean existsActiveUserById(Long user_id);

    @Query("SELECT u FROM User u WHERE u.isEmailConfirmed = true AND u.isActivated = false")
    List<User> findPendingUsers();

    void deleteUserByEmail(String email);
}
