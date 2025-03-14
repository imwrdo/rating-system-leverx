package org.leverx.ratingapp.enums;

import lombok.Getter;

@Getter
public enum Role {
    SELLER("SELLER"), ADMIN("ADMIN");

    private final String valueOfRole;

    Role(String valueOfRole) {
        this.valueOfRole = valueOfRole;
    }

}
