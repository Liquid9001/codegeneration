package nl.codegeneratie.els.exceptions;

public class IbanNotFoundException extends RuntimeException {
  public IbanNotFoundException(String iban) {
    super("Account not found with IBAN: " + iban);
  }
}
