# Coworking Booking App

Enlace a la App:

Aplicación fullstack para la gestión de reservas de espacios y recursos en un entorno coworking.

## 🚀 Descripción

Este proyecto consiste en el desarrollo de una aplicación desacoplada compuesta por:

- Backend REST API desarrollado con Spring Boot
- Frontend desarrollado con React
- Base de datos PostgreSQL

El objetivo principal es permitir la gestión de reservas de salas y estudios mediante un sistema seguro basado en autenticación JWT.

---

# 🧱 Arquitectura

El proyecto sigue una arquitectura desacoplada:

Frontend (React)
↓
REST API (Spring Boot)
↓
PostgreSQL

---

# 🔧 Backend

Desarrollado con:

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA / Hibernate
- PostgreSQL
- Swagger/OpenAPI

## ✔️ Funcionalidades implementadas

- Registro y login de usuarios
- Autenticación mediante JWT
- Roles USER / ADMIN
- CRUD de recursos
- Sistema de reservas
- Validación de solapamientos
- Gestión de disponibilidad
- Validaciones y manejo global de errores
- Tests básicos

## 📡 Endpoints principales

### Auth
- POST `/auth/register`
- POST `/auth/login`

### Resources
- GET `/resources`
- POST `/resources`

### Bookings
- POST `/bookings`
- GET `/bookings/me`
- DELETE `/bookings/{id}`

### 📄 Documentación de API

La API REST incluye documentación interactiva mediante Swagger/OpenAPI, permitiendo visualizar y probar los diferentes endpoints desde una interfaz gráfica sencilla.

Gracias a Swagger es posible:

- Consultar endpoints disponibles
- Probar requests directamente desde el navegador
- Visualizar parámetros y respuestas
- Facilitar la integración frontend/backend


---

# 🌐 Frontend

Frontend desarrollado con React consumiendo la API REST del backend.

## ✔️ Funcionalidades

- Login de usuarios
- Consumo de endpoints REST
- Gestión de JWT
- Visualización de recursos
- Creación de reservas

---

# 🗄️ Base de datos

- PostgreSQL
- Desplegada en Neon

---

# 📚 Objetivos del proyecto

- Aprender arquitectura desacoplada frontend/backend
- Implementar autenticación segura con JWT
- Consumir APIs REST desde React
- Gestionar estado y autenticación en frontend
- Trabajar con persistencia y relaciones mediante JPA/Hibernate

---

# 🚀 Tecnologías utilizadas

## Backend
- Java
- Spring Boot
- Spring Security
- JWT
- Hibernate / JPA
- PostgreSQL

## Frontend
- React
- Vite
- Axios

---

# 🔮 Futuras mejoras

- Dockerización
- Deploy cloud
- Integración Stripe
- Calendario visual
- Reservas recurrentes
- Panel administrador
