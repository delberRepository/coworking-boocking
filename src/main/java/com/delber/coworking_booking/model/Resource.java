package com.delber.coworking_booking.model;

import jakarta.persistence.*;

@Table(name="Recursos")
@Entity

public class Resource {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String name;
    @Enumerated(EnumType.STRING)
    private ResourceType type;
    //ROOM ,STUDIO ,SERVICE
    private Integer capacity;
    private boolean active;
}
