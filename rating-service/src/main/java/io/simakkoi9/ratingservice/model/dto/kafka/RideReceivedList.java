package io.simakkoi9.ratingservice.model.dto.kafka;

import java.util.List;

public record RideReceivedList(
    String personId,

    List<String> rideList
) {}
