package com.delber.coworking_booking.service;

import com.delber.coworking_booking.model.Booking;
import com.delber.coworking_booking.model.BookingStatus;
import com.delber.coworking_booking.model.Resource;
import com.delber.coworking_booking.model.ResourceType;
import com.delber.coworking_booking.model.Role;
import com.delber.coworking_booking.model.User;
import com.delber.coworking_booking.repository.IBookingRepository;
import com.delber.coworking_booking.repository.IResourcesRepository;
import com.delber.coworking_booking.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private IBookingRepository bookingRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IResourcesRepository resourceRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBookingShouldThrowWhenStartIsAfterEnd() {
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(1L, 1L, start, end));

        assertEquals("Rango de tiempo invalido", ex.getMessage());
        verify(resourceRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingShouldThrowWhenResourceDoesNotExist() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        when(resourceRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(1L, 99L, start, end));

        assertEquals("Recursos no encontrados", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingShouldThrowWhenThereIsAnOverlap() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        Resource resource = buildResource(10L);

        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(bookingRepository.findOverlappingBookings(10L, start, end))
                .thenReturn(List.of(new Booking()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(1L, 10L, start, end));

        assertEquals("Franja horaria no disponible", ex.getMessage());
        verify(userRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingShouldThrowWhenUserDoesNotExist() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        Resource resource = buildResource(10L);

        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(bookingRepository.findOverlappingBookings(10L, start, end)).thenReturn(List.of());
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.createBooking(3L, 10L, start, end));

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingShouldSaveConfirmedBooking() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        Resource resource = buildResource(10L);
        User user = buildUser(5L);

        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(bookingRepository.findOverlappingBookings(10L, start, end)).thenReturn(List.of());
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.createBooking(5L, 10L, start, end);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking savedBooking = captor.getValue();

        assertNotNull(result);
        assertEquals(user, savedBooking.getUser());
        assertEquals(resource, savedBooking.getResource());
        assertEquals(start, savedBooking.getStartTime());
        assertEquals(end, savedBooking.getEndTime());
        assertEquals(BookingStatus.CONFIRMED, savedBooking.getStatus());
    }

    @Test
    void updateBookingShouldThrowWhenUserIsNotOwner() {
        LocalDateTime start = LocalDateTime.now().plusHours(3);
        LocalDateTime end = LocalDateTime.now().plusHours(4);
        Booking booking = new Booking();
        booking.setId(12L);
        booking.setUser(buildUser(1L));

        when(bookingRepository.findById(12L)).thenReturn(Optional.of(booking));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.updateBooking(12L, 10L, 2L, start, end));

        assertEquals("No autorizado", ex.getMessage());
        verify(resourceRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingShouldThrowWhenBookingDoesNotExist() {
        LocalDateTime start = LocalDateTime.now().plusHours(3);
        LocalDateTime end = LocalDateTime.now().plusHours(4);

        when(bookingRepository.findById(12L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.updateBooking(12L, 10L, 2L, start, end));

        assertEquals("Reserva no encontrada", ex.getMessage());
        verify(resourceRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingShouldThrowWhenStartIsAfterEnd() {
        LocalDateTime start = LocalDateTime.now().plusHours(5);
        LocalDateTime end = LocalDateTime.now().plusHours(4);
        Booking booking = new Booking();
        booking.setId(12L);
        booking.setUser(buildUser(2L));

        when(bookingRepository.findById(12L)).thenReturn(Optional.of(booking));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.updateBooking(12L, 10L, 2L, start, end));

        assertEquals("Rango de tiempo invalido", ex.getMessage());
        verify(resourceRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingShouldThrowWhenResourceDoesNotExist() {
        LocalDateTime start = LocalDateTime.now().plusHours(3);
        LocalDateTime end = LocalDateTime.now().plusHours(4);
        Booking booking = new Booking();
        booking.setId(12L);
        booking.setUser(buildUser(2L));

        when(bookingRepository.findById(12L)).thenReturn(Optional.of(booking));
        when(resourceRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.updateBooking(12L, 10L, 2L, start, end));

        assertEquals("Recurso no encontrado", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingShouldThrowWhenThereIsAnOverlap() {
        LocalDateTime start = LocalDateTime.now().plusHours(3);
        LocalDateTime end = LocalDateTime.now().plusHours(4);
        Booking booking = new Booking();
        booking.setId(12L);
        booking.setUser(buildUser(2L));
        Resource resource = buildResource(10L);

        when(bookingRepository.findById(12L)).thenReturn(Optional.of(booking));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(bookingRepository.findOverlappingBookingsExcludingCurrent(10L, 12L, start, end))
                .thenReturn(List.of(new Booking()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.updateBooking(12L, 10L, 2L, start, end));

        assertEquals("Franja horaria no disponible", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingShouldSaveUpdatedBooking() {
        LocalDateTime start = LocalDateTime.now().plusHours(3);
        LocalDateTime end = LocalDateTime.now().plusHours(4);
        Booking booking = new Booking();
        booking.setId(12L);
        booking.setUser(buildUser(2L));
        booking.setStatus(BookingStatus.CANCELLED);
        Resource resource = buildResource(10L);

        when(bookingRepository.findById(12L)).thenReturn(Optional.of(booking));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource));
        when(bookingRepository.findOverlappingBookingsExcludingCurrent(10L, 12L, start, end))
                .thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.updateBooking(12L, 10L, 2L, start, end);

        assertNotNull(result);
        assertEquals(resource, result.getResource());
        assertEquals(start, result.getStartTime());
        assertEquals(end, result.getEndTime());
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void cancelBookingShouldThrowWhenUserIsNotOwner() {
        Booking booking = new Booking();
        booking.setId(8L);
        booking.setUser(buildUser(1L));

        when(bookingRepository.findById(8L)).thenReturn(Optional.of(booking));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.cancelBooking(8L, 2L));

        assertEquals("Not allowed", ex.getMessage());
    }

    @Test
    void cancelBookingShouldMarkBookingAsCancelled() {
        Booking booking = new Booking();
        booking.setId(8L);
        booking.setUser(buildUser(2L));
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(8L)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(8L, 2L);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertTrue(booking.getUser().getId().equals(2L));
    }

    private Resource buildResource(Long id) {
        Resource resource = new Resource();
        resource.setId(id);
        resource.setName("Sala 1");
        resource.setType(ResourceType.ROOM);
        resource.setCapacity(8);
        resource.setActive(true);
        return resource;
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user@example.com");
        user.setPassword("secret");
        user.setRole(Role.USER);
        return user;
    }
}
