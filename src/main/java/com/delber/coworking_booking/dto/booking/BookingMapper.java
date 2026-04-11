package com.delber.coworking_booking.dto.booking;

import com.delber.coworking_booking.model.Booking;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        BookingResponse dto = new BookingResponse();

        dto.setId(booking.getId());
        dto.setResourceId(booking.getResource().getId());
        dto.setResourceName(booking.getResource().getName());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus().name());
        dto.setUserId(booking.getUser().getId());

        return dto;
    }
}