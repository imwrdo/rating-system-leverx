package org.leverx.ratingapp.enums;

import lombok.Getter;


@Getter
public enum Status {
    CREATED("Created"),
    DELETED("Deleted"),
    UPDATED("Updated"),
    ACTIVE("Active"),
    APPROVED("Approved"),
    AUTHENTICATED("Authenticated"),
    PENDING("Pending"),
    SENT("Sent");

    private final String valueOfStatus;

    Status(String valueOfStatus) {
        this.valueOfStatus = valueOfStatus;
    }

}
