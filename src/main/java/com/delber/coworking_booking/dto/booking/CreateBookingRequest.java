package com.delber.coworking_booking.dto.booking;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        Long resourceId,
        LocalDateTime start,
        LocalDateTime end
) {}
//clase con los datos del cliente, de la peticion que se hace al servidor