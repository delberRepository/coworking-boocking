package com.delber.coworking_booking.dto.booking;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateBookingRequest {

    private Long resourceId;
    private LocalDateTime start;
    private LocalDateTime end;

}
//clase con los datos del cliente, de la peticion que se hace al servidor