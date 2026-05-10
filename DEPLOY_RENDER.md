# Despliegue en Render

Este repositorio queda preparado para desplegar:

- backend Spring Boot en un `Web Service` con Docker
- frontend React/Vite en un `Static Site`
- base de datos PostgreSQL en Neon

## 1. Variables del backend

En el servicio `coworking-booking-api` configura:

- `DB_URL`: URL JDBC de Neon, por ejemplo `jdbc:postgresql://ep-xxxx.eu-central-1.aws.neon.tech/neondb?sslmode=require`
- `DB_USERNAME`: usuario de Neon
- `DB_PASSWORD`: password de Neon
- `JWT_SECRET`: Render lo genera automáticamente si creas el servicio con `render.yaml`
- `CORS_ALLOWED_ORIGINS`: URL publica del frontend en Render, por ejemplo `https://coworking-booking-frontend.onrender.com`

## 2. Variables del frontend

En el servicio `coworking-booking-frontend` configura:

- `VITE_API_BASE_URL`: URL publica del backend, por ejemplo `https://coworking-booking-api.onrender.com`

## 3. Crear los servicios

Opcion recomendada:

1. En Render, `New +` -> `Blueprint`
2. Selecciona este repo
3. Render detectara `render.yaml`
4. Completa las variables pendientes
5. Lanza el deploy

## 4. Orden practico

Como el frontend necesita conocer la URL final del backend:

1. Despliega el backend
2. Copia su URL publica de Render
3. Pon esa URL en `VITE_API_BASE_URL` del frontend
4. Despliega o redepliega el frontend
5. Copia la URL del frontend y ponla en `CORS_ALLOWED_ORIGINS` del backend
6. Redepliega el backend

## 5. Comprobaciones

- Health backend: `/health`
- Swagger: `/swagger-ui/index.html`
- Frontend: carga la pantalla de login/registro
