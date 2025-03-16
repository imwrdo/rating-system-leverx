package org.leverx.ratingapp.services.user;

import org.leverx.ratingapp.dtos.user.UserRankingDTO;
import org.leverx.ratingapp.dtos.user.UserDTO;

import java.util.List;

/**
 * Service  for managing user details
 */
public interface UserService {
     // Enables a user by their email address
     void enableUser(String email);

     // Retrieves a list of user rankings, sorted by their rating. Optionally filters rankings by game name.
     List<UserRankingDTO> getUserRating(String gameName,Long ratingLimit);

     // Retrieves a list of all users. Filters users based on their active status
     List<UserDTO> getAllUsers(boolean onlyActive, boolean isAdmin);

     // Retrieves a user by their ID, with an option to filter by active status
     UserDTO getUserById(Long user_id, boolean onlyActive);

     // Retrieves a list of inactive users
     List<UserDTO> getInactiveUsers();

     // Retrieves a list of users whose registration is pending
     List<UserDTO> getPendingUsers();
}
