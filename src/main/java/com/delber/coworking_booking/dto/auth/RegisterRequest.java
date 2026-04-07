package com.delber.coworking_booking.dto.auth;

//este DTO es para controlar los datos que se piden al cliente de la entidad
//User, por ejemplo, no se le permite ni se le pide Role, se le asigna por defecto USER
public record RegisterRequest(String email, String password) {}
