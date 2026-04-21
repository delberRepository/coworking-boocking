package com.delber.coworking_booking.dto.booking;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public record BookingResponse (
        Long id,
        Long resourceId,
        String resourceName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status,
        Long userId
) {}