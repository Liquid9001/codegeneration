package nl.codegeneratie.els.dtos.BankAccount;

import lombok.Data;
import nl.codegeneratie.els.domain.User;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BankAccountRequest {
    private Long id;
    private String iban;
    private String name;
    private BigDecimal balance;
    private boolean isActive;
    private User user;
}
