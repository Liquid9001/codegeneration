package nl.codegeneratie.els.dtos.BankAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.codegeneratie.els.domain.Account;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountResponse {
    private Long id;
    private String iban;
    private String name;
    private BigDecimal balance;
    private boolean isActive;
    private String accountType;
    private BigDecimal absoluteLimit;

    public static BankAccountResponse toDto(Account account) {
        return BankAccountResponse.builder()
                .id(account.getId())
                .iban(account.getIban())
                .balance(account.getBalance())
                .isActive(account.isActive())
                .accountType(account.getAccountType().name())
                .build();
    }
}
