package nl.codegeneratie.els.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("Forbidden");
    }
}