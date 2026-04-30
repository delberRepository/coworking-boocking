package com.delber.coworking_booking.controller;

import com.delber.coworking_booking.dto.booking.BookingMapper;
import com.delber.coworking_booking.dto.booking.BookingResponse;
import com.delber.coworking_booking.exception.GlobalExceptionHandler;
import com.delber.coworking_booking.model.Booking;
import com.delber.coworking_booking.model.BookingStatus;
import com.delber.coworking_booking.model.Resource;
import com.delber.coworking_booking.model.ResourceType;
import com.delber.coworking_booking.model.Role;
import com.delber.coworking_booking.model.User;
import com.delber.coworking_booking.security.JwtAuthenticationFilter;
import com.delber.coworking_booking.service.BookingService;
import com.delber.coworking_booking.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private BookingMapper bookingMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createBookingShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        String body = """
                {
                  "resourceId": null,
                  "start": null,
                  "end": null
                }
                """;

        mockMvc.perform(post("/bookings")
                        .principal(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Datos de entrada no validos"))
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields.length()").value(3));
    }

    @Test
    void createBookingShouldReturnConflictWhenServiceThrowsOverlapException() throws Exception {
        String body = """
                {
                  "resourceId": 1,
                  "start": "2099-04-25T10:00:00",
                  "end": "2099-04-25T12:00:00"
                }
                """;

        when(bookingService.createBooking(anyLong(), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Franja horaria no disponible"));

        mockMvc.perform(post("/bookings")
                        .principal(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Franja horaria no disponible"));
    }

    @Test
    void createBookingShouldReturnOkWhenRequestIsValid() throws Exception {
        String body = """
                {
                  "resourceId": 1,
                  "start": "2099-04-25T10:00:00",
                  "end": "2099-04-25T12:00:00"
                }
                """;

        BookingResponse response = new BookingResponse(
                7L,
                1L,
                "Sala 1",
                LocalDateTime.parse("2099-04-25T10:00:00"),
                LocalDateTime.parse("2099-04-25T12:00:00"),
                "CONFIRMED",
                2L
        );
        Booking booking = new Booking();
        Resource resource = new Resource();
        User user = new User();
        resource.setId(1L);
        resource.setName("Sala 1");
        resource.setType(ResourceType.SALA_DANZA);
        user.setId(2L);
        booking.setId(7L);
        booking.setResource(resource);
        booking.setUser(user);
        booking.setStartTime(LocalDateTime.parse("2099-04-25T10:00:00"));
        booking.setEndTime(LocalDateTime.parse("2099-04-25T12:00:00"));
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingService.createBooking(anyLong(), anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(booking);
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .principal(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.resourceId").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    private UsernamePasswordAuthenticationToken authenticatedUser() {
        User user = new User();
        user.setId(2L);
        user.setEmail("user@example.com");
        user.setPassword("secret");
        user.setRole(Role.USER);

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}
