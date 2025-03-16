package org.leverx.ratingapp.models.enums;

import lombok.Getter;

/**
 * Enum to represent the user roles in the system.
 * The roles define the permissions and behavior of the users in the application.
 * - SELLER: A user who can list products, rate other sellers, and have ratings from buyers.
 * - ADMIN: A user who has higher privileges, such as managing users and controlling system behavior.
 */
@Getter
public enum Role {
    SELLER("SELLER"),  // Represents a seller who can interact with the system as a vendor.
    ADMIN("ADMIN");    // Represents an administrator who has control over the system.

    private final String valueOfRole; // Holds the string representation of the role (e.g., "SELLER", "ADMIN").

    /**
     * Constructor for the enum. It initializes the value of the role.
     * @param valueOfRole The string representation of the role.
     */
    Role(String valueOfRole) {
        this.valueOfRole = valueOfRole;
    }

}
