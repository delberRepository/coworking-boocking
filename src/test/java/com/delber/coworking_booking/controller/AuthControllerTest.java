package com.delber.coworking_booking.controller;

import com.delber.coworking_booking.exception.GlobalExceptionHandler;
import com.delber.coworking_booking.model.User;
import com.delber.coworking_booking.repository.IUserRepository;
import com.delber.coworking_booking.security.JwtAuthenticationFilter;
import com.delber.coworking_booking.service.CustomUserDetailsService;
import com.delber.coworking_booking.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void registerShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        String body = """
                {
                  "email": "correo-invalido",
                  "password": "123"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Datos de entrada no validos"))
                .andExpect(jsonPath("$.fields").isArray());
    }

    @Test
    void registerShouldReturnOkWhenBodyIsValid() throws Exception {
        String body = """
                {
                  "email": "user@example.com",
                  "password": "123456"
                }
                """;

        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario registrado correctamente"));

        verify(passwordEncoder).encode("123456");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void loginShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        String body = """
                {
                  "email": "",
                  "password": ""
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Datos de entrada no validos"));
    }

    @Test
    void loginShouldReturnTokenWhenBodyIsValid() throws Exception {
        String body = """
                {
                  "email": "user@example.com",
                  "password": "123456"
                }
                """;

        when(authService.login(any())).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));

        verify(authService).login(any());
    }
}
