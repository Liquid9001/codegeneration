package nl.codegeneratie.els.exceptions;

import java.math.BigDecimal;

public class InvalidTransferLimitsException extends RuntimeException {
  public InvalidTransferLimitsException(BigDecimal daily, BigDecimal absolute) {
    super(String.format("Invalid transfer limits - Daily: %s, Absolute: %s", daily, absolute));
  }
}
