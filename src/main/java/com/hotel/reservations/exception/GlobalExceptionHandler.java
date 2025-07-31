package com.hotel.reservations.exception;

import com.hotel.reservations.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException ex) {
        ApiResponse<?> response = new ApiResponse<>(null, "ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors()
                .stream().map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        ApiResponse<?> response = new ApiResponse<>(null, "ERROR", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraint(ConstraintViolationException ex) {
        String mensaje = ex.getConstraintViolations()
                .stream().map(v -> v.getMessage())
                .collect(Collectors.joining(" | "));

        ApiResponse<?> response = new ApiResponse<>(null, "ERROR", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex) {
        ApiResponse<?> response = new ApiResponse<>(null, "ERROR", "Ha ocurrido un error inesperado.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}