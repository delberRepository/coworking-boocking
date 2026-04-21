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

        return new BookingResponse(
                booking.getId(),
                booking.getResource().getId(),
                booking.getResource().getName(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus().name(),
                booking.getUser().getId()
        );
    }
}