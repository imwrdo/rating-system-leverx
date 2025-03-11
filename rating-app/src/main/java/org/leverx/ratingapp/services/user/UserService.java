package org.leverx.ratingapp.services.user;

import org.leverx.ratingapp.dtos.user.UserRankingDTO;
import org.leverx.ratingapp.dtos.user.UserDTO;

import java.util.List;

public interface UserService {
     void enableUser(String email);
     List<UserRankingDTO> getUserRating(String gameName,Long ratingLimit);
     List<UserDTO> getAllUsers(boolean onlyActive, boolean isAdmin);
     UserDTO getUserById(Long user_id, boolean onlyActive);
     List<UserDTO> getInactiveUsers();
     List<UserDTO> getPendingUsers();
}
