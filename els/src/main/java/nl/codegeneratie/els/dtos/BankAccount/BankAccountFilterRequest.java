package nl.codegeneratie.els.dtos.BankAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.codegeneratie.els.domain.enums.AccountType;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountFilterRequest {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String iban;
    private AccountType accountType;

    @Builder.Default
    private Sort.Direction sortDirection = Sort.Direction.DESC;

    @Builder.Default
    private int offset = 0;
}

