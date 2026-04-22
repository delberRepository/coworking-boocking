package com.delber.coworking_booking.dto.resources;

public record CreateResourceRequest(
        String name,
        String type,
        Integer capacity,
        Boolean active
) {}
