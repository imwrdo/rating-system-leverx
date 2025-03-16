package org.leverx.ratingapp.services.auth.authorization;

import org.leverx.ratingapp.models.entities.User;

/**
 * AuthorizationService interface defines methods for handling authorization logic.
 * It provides functionalities to fetch the current user and authorize resource modifications
 * based on the current user's identity.
 */
public interface AuthorizationService {
     // Retrieves the currently authenticated user from the security context.
    User getCurrentUser();

    // Retrieves the currently authenticated user, throwing an exception if the user is not authenticated.
    User getRequiredCurrentUser();

    // Authorizes the current user to modify a specific resource (either a Comment or a GameObject).
    void authorizeResourceModification(Object entity, User currentUser);
}
