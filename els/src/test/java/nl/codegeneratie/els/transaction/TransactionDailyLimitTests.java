package nl.codegeneratie.els.transaction;

import nl.codegeneratie.els.domain.Account;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class TransactionDailyLimitTests extends AbstractTransactionIntegrationTest {

    @Test
    void allowsTransfersUpToAndIncludingTheDailyLimit() {
        Account sender = createAccount("500.00", "500.00", "150.00");
        Account receiver = createAccount("100.00", "500.00", "500.00");

        transactionService.transfer(transfer(sender, receiver, "100.00"));
        transactionService.transfer(transfer(sender, receiver, "50.00"));

        assertAmount("350.00", getAccount(sender).getBalance());
        assertAmount("250.00", getAccount(receiver).getBalance());
    }

    @Test
    void rejectsTransferThatWouldExceedAccumulatedDailyLimit() {
        Account sender = createAccount("500.00", "500.00", "150.00");
        Account receiver = createAccount("100.00", "500.00", "500.00");

        transactionService.transfer(transfer(sender, receiver, "100.00"));

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(transfer(sender, receiver, "50.01")));
        assertAmount("400.00", getAccount(sender).getBalance());
        assertAmount("200.00", getAccount(receiver).getBalance());
    }
}
