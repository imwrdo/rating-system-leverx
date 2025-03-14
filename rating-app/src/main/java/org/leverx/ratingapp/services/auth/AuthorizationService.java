package org.leverx.ratingapp.services.auth;

import lombok.RequiredArgsConstructor;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.exceptions.ForbiddenException;
import org.leverx.ratingapp.exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof User 
                ? (User) authentication.getPrincipal() 
                : null;
    }

    public User getRequiredCurrentUser() {
        User user = getCurrentUser();
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        return user;
    }

    public void authorizeResourceModification(Object entity, User currentUser) {
        User resourceOwner = null;
        
        if (entity instanceof Comment comment) {
            resourceOwner = comment.getAuthor();
        } else if (entity instanceof GameObject gameObject) {
            resourceOwner = gameObject.getUser();
        }
        
        if (resourceOwner == null || !resourceOwner.getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to modify this resource");
        }
    }
}
