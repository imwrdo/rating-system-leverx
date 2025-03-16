package org.leverx.ratingapp.models.enums;

import lombok.Getter;

/**
 * Enum to represent various statuses that can be applied to different entities or actions.
 * These statuses can help track the state of objects like comments, game objects, or user actions.
 *
 * - CREATED: Represents a newly created entity or object.
 * - DELETED: Represents an entity or object that has been deleted.
 * - UPDATED: Represents an entity or object that has been updated.
 * - ACTIVE: Represents an entity or object that is currently active.
 * - APPROVED: Represents an entity or object that has been approved.
 * - AUTHENTICATED: Represents a successful authentication process.
 * - PENDING: Represents an entity or object that is in a pending state.
 * - SENT: Represents an entity or object that has been sent or dispatched.
 */
@Getter
public enum Status {
    CREATED("Created"),     // Newly created entity
    DELETED("Deleted"),     // Entity has been deleted
    UPDATED("Updated"),     // Entity has been updated
    ACTIVE("Active"),       // Entity is active and operational
    APPROVED("Approved"),   // Entity has been approved
    AUTHENTICATED("Authenticated"), // Represents successful authentication
    PENDING("Pending"),     // Entity is in a pending state (waiting for approval or action)
    SENT("Sent");           // Entity has been sent or dispatched

    private final String valueOfStatus; // String value representing the status

    /**
     * Constructor for the enum. Initializes the status with a string value.
     * @param valueOfStatus The string representation of the status (e.g., "Created").
     */
    Status(String valueOfStatus) {
        this.valueOfStatus = valueOfStatus;
    }

}
