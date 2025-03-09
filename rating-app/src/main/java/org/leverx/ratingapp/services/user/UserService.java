package org.leverx.ratingapp.services.user;

import org.leverx.ratingapp.dtos.user.UserRankingDTO;
import org.leverx.ratingapp.dtos.user.UserDTO;

import java.util.List;

public interface UserService {
     void enableUser(String email);
     List<UserDTO> getAllActivatedUsers();
     UserDTO getActiveUser(Long sellerId);
     List<UserRankingDTO> getUserRating(String gameName,Long ratingLimit);
}
