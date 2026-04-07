package com.delber.coworking_booking.model;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Boocking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToMany
    private User user;
    @Column(name="Recursos")
    @ManyToMany
    private Resource resources;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    private BoockingStatus   status;

}
