package com.delber.coworking_booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



    @Entity
    @Table(name = "Usuarios")
    @Getter
    @Setter
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(unique = true, nullable = false)
        private String email;
        @Column(nullable = false)
        private String password;

        @Enumerated(EnumType.STRING)
        private Role role;
    }

