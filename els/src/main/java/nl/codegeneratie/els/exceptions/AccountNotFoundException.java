package nl.codegeneratie.els.exceptions;

public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException(Long id) {
    super("Account not found with id: " + id);
  }

  // a generic one that can be used for everything else, for example when we want to throw an exception with a custom message.
  public AccountNotFoundException(String message) {
    super(message);
  }
}
