package com.delber.coworking_booking.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        ValidationError error = new ValidationError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Datos de entrada no validos",
                request.getRequestURI(),
                LocalDateTime.now(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Valor no valido: " + ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = resolveStatus(ex.getMessage());

        ApiError error = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(error);
    }

    private HttpStatus resolveStatus(String message) {
        if (message == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        if (message.contains("no encontrado") || message.contains("no encontrados")) {
            return HttpStatus.NOT_FOUND;
        }

        if (message.contains("No autorizado") || message.contains("Not allowed")) {
            return HttpStatus.FORBIDDEN;
        }

        if (message.contains("Rango de tiempo invalido")) {
            return HttpStatus.BAD_REQUEST;
        }

        if (message.contains("Franja horaria no disponible")) {
            return HttpStatus.CONFLICT;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public record ApiError(
            int status,
            String error,
            String message,
            String path,
            LocalDateTime timestamp
    ) {
    }

    public record ValidationError(
            int status,
            String error,
            String message,
            String path,
            LocalDateTime timestamp,
            List<FieldError> fields
    ) {
    }

    public record FieldError(
            String field,
            String message
    ) {
    }
}
