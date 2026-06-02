package nl.codegeneratie.els.exceptions;

import nl.codegeneratie.els.dtos.ApiError;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void returnsNotFoundForMissingTransactions() {
        ResponseEntity<ApiError> response =
                handler.handleNotFoundExceptions(new TransactionNotFoundException(42L));

        assertError(response, HttpStatus.NOT_FOUND, "Transaction not found with id: 42");
    }

    @Test
    void returnsBadRequestForInvalidArguments() {
        ResponseEntity<ApiError> response =
                handler.handleBadRequestExceptions(new IllegalArgumentException("Invalid amount"));

        assertError(response, HttpStatus.BAD_REQUEST, "Invalid amount");
    }

    @Test
    void returnsUnauthorizedForInvalidCredentials() {
        ResponseEntity<ApiError> response =
                handler.handleInvalidCredentialsException(new InvalidCredentialsException());

        assertError(response, HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @Test
    void doesNotExposeUnexpectedExceptionDetails() {
        ResponseEntity<ApiError> response =
                handler.handleGeneralExceptions(new RuntimeException("database password leaked"));

        assertError(response, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    private void assertError(ResponseEntity<ApiError> response, HttpStatus status, String message) {
        ApiError body = response.getBody();

        assertNotNull(body);
        assertEquals(status, response.getStatusCode());
        assertEquals(status.value(), body.status());
        assertEquals(status.getReasonPhrase(), body.error());
        assertEquals(message, body.message());
        assertNotNull(body.timestamp());
    }
}
