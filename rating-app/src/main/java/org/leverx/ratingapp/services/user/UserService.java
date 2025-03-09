package org.leverx.ratingapp.services.user;

import org.leverx.ratingapp.dtos.UserDTO;

import java.util.List;

public interface UserService {
     void enableUser(String email);
     List<UserDTO> getAllActivatedUsers();

}
