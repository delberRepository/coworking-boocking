package com.delber.coworking_booking.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/user")
    public String userEndpoint() {
        return "Hola USER";
    }

    @GetMapping("/admin")
    public Object adminEndpoint(Authentication auth) {
        return auth.getAuthorities();
    }
    @GetMapping("/whoami")
    public Object whoami(Authentication auth) {
        return auth.getAuthorities();
    }
}
