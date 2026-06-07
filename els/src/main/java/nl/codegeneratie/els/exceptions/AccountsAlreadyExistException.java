package nl.codegeneratie.els.exceptions;

import nl.codegeneratie.els.domain.User;

public class AccountsAlreadyExistException extends RuntimeException {
    public AccountsAlreadyExistException(User user) {
        super(String.format("User %s already has default accounts.", user.getId()));
    }
}