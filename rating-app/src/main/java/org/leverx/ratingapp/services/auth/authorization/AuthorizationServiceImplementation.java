package org.leverx.ratingapp.services.auth.authorization;

import lombok.RequiredArgsConstructor;
import org.leverx.ratingapp.models.entities.Comment;
import org.leverx.ratingapp.models.entities.GameObject;
import org.leverx.ratingapp.models.entities.User;
import org.leverx.ratingapp.exceptions.ForbiddenException;
import org.leverx.ratingapp.exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * AuthorizationServiceImplementation is the concrete implementation of the {@link AuthorizationService} interface.
 * It handles the logic related to the current authenticated user and resource access control.
 */
@Service
@RequiredArgsConstructor
public class AuthorizationServiceImplementation implements AuthorizationService {

    /**
     * Retrieves the current authenticated user from the SecurityContext.
     *
     * @return the current authenticated {@link User}, or null if no user is authenticated.
     */
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof User 
                ? (User) authentication.getPrincipal() 
                : null;
    }
    /**
     * Retrieves the currently authenticated user, throwing an exception if the user is not authenticated.
     *
     * @return the authenticated User.
     * @throws UnauthorizedException if the user is not authenticated.
     */
    @Override
    public User getRequiredCurrentUser() {
        User user = getCurrentUser();
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        return user;
    }
    /**
     * Authorizes the current user to modify a specific resource (either a {@link Comment} or a {@link GameObject}).
     * Throws an exception if the current user is not the owner of the resource.
     *
     * @param entity the resource ({@link Comment} or {@link GameObject}) that is being modified.
     * @param currentUser the current authenticated {@link User} who is attempting to modify the resource.
     * @throws ForbiddenException if the current user is not authorized to modify the resource.
     */
    @Override
    public void authorizeResourceModification(Object entity, User currentUser) {
        User resourceOwner = null;
        // Determine the resource owner based on the entity type
        if (entity instanceof Comment comment) {
            resourceOwner = comment.getAuthor();
        } else if (entity instanceof GameObject gameObject) {
            resourceOwner = gameObject.getUser();
        }
        // If the resource owner is null or does not match the current user's ID, throw an exception
        if (resourceOwner == null || !resourceOwner.getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to modify this resource");
        }
    }
}
