package io.simakkoi9.ridesservice.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public enum RideStatus {
    CREATED(1),
    ACCEPTED(2),
    EN_ROUTE_TO_PASSENGER(3),
    EN_ROUTE_TO_DESTINATION(4),
    COMPLETED(5),
    CANCELLED_BY_PASSENGER(6),
    CANCELLED_BY_DRIVER(7);

    private final int code;

    RideStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static List<RideStatus> getBusyPassengerStatusList() {
        return List.of(CREATED, ACCEPTED, EN_ROUTE_TO_PASSENGER, EN_ROUTE_TO_DESTINATION);
    }

    public static List<RideStatus> getBusyDriverStatusList() {
        return List.of(ACCEPTED, EN_ROUTE_TO_PASSENGER, EN_ROUTE_TO_DESTINATION);
    }

    public static List<RideStatus> getImmutableStatusList() {
        return List.of(RideStatus.COMPLETED, RideStatus.CANCELLED_BY_DRIVER, RideStatus.EN_ROUTE_TO_PASSENGER);
    }

    @JsonCreator
    public static RideStatus fromValue(String value) {
        for (RideStatus rideStatus : RideStatus.values()) {
            if (rideStatus.name().equalsIgnoreCase(value)) {
                return rideStatus;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown status value: %s", value));
    }

    public static RideStatus fromCode(int code) {
        for (RideStatus rideStatus : RideStatus.values()) {
            if (rideStatus.getCode() == code) {
                return rideStatus;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown status code: %d", code));
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
