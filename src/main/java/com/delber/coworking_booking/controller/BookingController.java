package com.delber.coworking_booking.controller;

import com.delber.coworking_booking.dto.booking.BookingMapper;
import com.delber.coworking_booking.dto.booking.BookingResponse;
import com.delber.coworking_booking.dto.booking.CreateBookingRequest;
import com.delber.coworking_booking.model.Booking;
import com.delber.coworking_booking.model.User;
import com.delber.coworking_booking.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bs;
    private final BookingMapper bm;

    public BookingController(BookingService bookingService,
                             BookingMapper bookingMapper) {
        this.bs = bookingService;
        this.bm = bookingMapper;
    }

    // 🔹 CREATE BOOKING
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            Authentication authentication
    ) {
//aqui se valida el user
        User user = (User) authentication.getPrincipal();
//aqui se crea un objeto booking, llamando al metodo de booking services
//pasandole los parametros que necesita
//en el propio metodo del servicio se puede ver que llama al metodo save del repobooking
        Booking booking = bs.createBooking(
                user.getId(),
                request.resourceId(),
                request.start(),
                request.end()
        );

        return ResponseEntity.ok(bm.toResponse(booking));
    }
    //esto siempre para actualizar
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody CreateBookingRequest request,
            Authentication authentication
    ) {

        User user = (User) authentication.getPrincipal();


        Booking booking = bs.updateBooking(
                id,
                request.resourceId(),
                user.getId(),
                request.start(),
                request.end()
        );

        return ResponseEntity.ok(bm.toResponse(booking));
    }

    // 🔹 GET MY BOOKINGS
    @GetMapping("/me")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            Authentication authentication
    ) {

        User user = (User) authentication.getPrincipal();

        List<Booking> bookings = bs.getMyBookings(user.getId());

        List<BookingResponse> response = bookings.stream()
                .map(bm::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // 🔹 CANCEL BOOKING
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User user = (User) authentication.getPrincipal();

        bs.cancelBooking(id, user.getId());

        return ResponseEntity.noContent().build();
    }
}
