package nl.codegeneratie.els.transaction;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.dtos.TransactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class TransactionAtmTests extends AbstractTransactionIntegrationTest {

    @BeforeEach
    void resetAtmBalance() {
        Account atm = getAtm();
        atm.setBalance(new BigDecimal("1000.00"));
        accountRepository.save(atm);
    }

    @Test
    void depositIncreasesCustomerAndAtmBalances() {
        Account customer = createAccount("100.00", "500.00", "500.00");
        TransactionDTO dto = new TransactionDTO();
        dto.setReceiverIban(customer.getIban());
        dto.setAmount(new BigDecimal("75.00"));
        dto.setTransactionType("DEPOSIT");

        transactionService.deposit(dto);

        assertAmount("175.00", getAccount(customer).getBalance());
        assertAmount("1075.00", getAtm().getBalance());
    }

    @Test
    void withdrawalDecreasesCustomerAndAtmBalances() {
        Account customer = createAccount("500.00", "500.00", "500.00");

        transactionService.withdrawal(transaction(customer, "75.00", "WITHDRAWAL"));

        assertAmount("425.00", getAccount(customer).getBalance());
        assertAmount("925.00", getAtm().getBalance());
    }

    @Test
    void withdrawalIsRejectedWhenAtmHasInsufficientCash() {
        Account atm = getAtm();
        atm.setBalance(new BigDecimal("50.00"));
        accountRepository.save(atm);
        Account customer = createAccount("500.00", "500.00", "500.00");

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.withdrawal(transaction(customer, "75.00", "WITHDRAWAL")));

        assertAmount("500.00", getAccount(customer).getBalance());
        assertAmount("50.00", getAtm().getBalance());
    }
}
