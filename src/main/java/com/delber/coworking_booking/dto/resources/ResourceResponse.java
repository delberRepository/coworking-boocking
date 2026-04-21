package com.delber.coworking_booking.dto.resources;

public record ResourceResponse(
        Long id,
        String name,
        String type,
        Integer capacity,
        Boolean active
) {}
