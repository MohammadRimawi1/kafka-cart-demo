package com.exalt.cart_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Central place that catches exceptions thrown anywhere in the
 * controller layer and converts them into clean, consistent JSON
 * error responses instead of raw stack traces.
 *
 * @RestControllerAdvice applies to every @RestController in the
 * application, so CartController doesn't need any try/catch of its
 * own -- exceptions just propagate up and get caught here.
 *
 * @author Mohammad Rimawi
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Thrown by CartService.getCartOrThrow when a cartId doesn't
     * exist in the store. Mapped to 404, since the client asked
     * for something that isn't there.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Thrown when a UUID path/query param can't be parsed
     * (e.g. cartId=not-a-uuid). Mapped to 400, since this is a
     * malformed request, not a missing resource.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Thrown when a required request param is missing entirely
     * (e.g. a future endpoint that requires cartId but it's absent).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Thrown when @RequestBody validation fails (relevant once
     * you add @Valid + Bean Validation annotations on CartItem,
     * e.g. @Min(1) on quantity). Collects all field errors into
     * one readable response instead of Spring's default verbose one.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation failed");
        body.put("fields", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Catch-all safety net for anything not explicitly handled above
     * (e.g. a NullPointerException from a bug, Kafka connection issues
     * surfacing synchronously, etc). Mapped to 500, and deliberately
     * does not leak the raw exception message/stack trace to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again.");
    }

    /**
     * Builds a consistent JSON error body across all handlers above:
     * { "timestamp": ..., "status": ..., "error": "..." }
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}