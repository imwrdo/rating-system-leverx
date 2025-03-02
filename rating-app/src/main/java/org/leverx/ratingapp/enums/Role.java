package org.leverx.ratingapp.enums;

import java.util.Arrays;

public enum Role {
    SELLER("SELLER"), ADMIN("ADMIN");

    private final String valueOfRole;

    Role(String valueOfRole) {
        this.valueOfRole = valueOfRole;
    }

    public String getValueOfRole() {
        return valueOfRole;
    }

    public Role getRoleFromString(String role) {
        return Arrays.stream(values())
                .filter(c -> c.getValueOfRole().equals(valueOfRole))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid character: " + valueOfRole));
    }
}
