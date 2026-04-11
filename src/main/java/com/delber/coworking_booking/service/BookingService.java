package com.delber.coworking_booking.service;

import com.delber.coworking_booking.model.Booking;
import com.delber.coworking_booking.model.BookingStatus;
import com.delber.coworking_booking.model.Resource;
import com.delber.coworking_booking.model.User;
import com.delber.coworking_booking.repository.IBookingRepository;
import com.delber.coworking_booking.repository.IResourcesRepository;
import com.delber.coworking_booking.repository.IUserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookingService {
    private final IBookingRepository br;
    private final IUserRepository ur;
    private final IResourcesRepository rr;

    @Transactional
    public Booking createBooking(
            Long userId,
            Long resourceId,
            LocalDateTime start,
            LocalDateTime end
    ) {

        // 1. Validación
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new RuntimeException("Rango de tiempo invalido");
        }

        // 2. Buscar resource
        Resource resource = rr.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Recursos no encontrados"));

        // 3. Verificar solapamientos
        List<Booking> solapamiento = br.findOverlappingBookings(resourceId, start, end);

        if (!solapamiento.isEmpty()) {
            throw new RuntimeException("Franja horaria no disponible");
        }

        // 4. Buscar usuario
        User user = ur.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 5. Crear booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setResource(resource);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setStatus(BookingStatus.CONFIRMED);

        return br.save(booking);
    }
    @Transactional(readOnly = true)
    public List<Booking> getMyBookings(Long userId) {
        return br.findByUserId(userId);
    }
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByResource(Long resourceId) {
        return br.findByResourceId(resourceId);
    }
    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {

        Booking booking = br.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking no encontrado"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not allowed");
        }

        booking.setStatus(BookingStatus.CANCELLED);
    }
}