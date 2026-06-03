package nl.codegeneratie.els.transaction;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.dtos.TransactionDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionRollbackTests extends AbstractTransactionIntegrationTest {

    @Test
    void rollsBackBalanceUpdatesWhenTransactionRecordCannotBeSaved() {
        Account sender = createAccount("500.00", "500.00", "500.00");
        Account receiver = createAccount("100.00", "500.00", "500.00");
        long transactionCountBefore = transactionRepository.count();

        try {
            TransactionDTO dto = transfer(sender, receiver, "75.00");
            dto.setDescription("x".repeat(1000));

            assertThrows(RuntimeException.class, () -> transactionService.transfer(dto));

            assertAmount("500.00", getAccount(sender).getBalance());
            assertAmount("100.00", getAccount(receiver).getBalance());
            assertEquals(transactionCountBefore, transactionRepository.count());
        } finally {
            accountRepository.deleteById(sender.getId());
            accountRepository.deleteById(receiver.getId());
        }
    }
}
