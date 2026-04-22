package com.delber.coworking_booking.controller;

import com.delber.coworking_booking.dto.auth.LoginRequest;
import com.delber.coworking_booking.dto.auth.RegisterRequest;
import com.delber.coworking_booking.model.Role;
import com.delber.coworking_booking.model.User;
import com.delber.coworking_booking.repository.IUserRepository;
import com.delber.coworking_booking.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserRepository us;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;


    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {


        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        us.save(user);

        return "Usuario registrado correctamente";
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}