package nl.codegeneratie.els.transaction;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class TransactionBalanceIntegrityTests extends AbstractTransactionIntegrationTest {

    @Test
    void transferMovesTheExactAmountWithoutChangingCombinedBalance() {
        Account sender = createAccount("500.00", "500.00", "500.00");
        Account receiver = createAccount("100.00", "500.00", "500.00");
        BigDecimal combinedBalanceBefore = sender.getBalance().add(receiver.getBalance());

        transactionService.transfer(transfer(sender, receiver, "75.00"));

        Account updatedSender = getAccount(sender);
        Account updatedReceiver = getAccount(receiver);
        assertAmount("425.00", updatedSender.getBalance());
        assertAmount("175.00", updatedReceiver.getBalance());
        assertEquals(0, combinedBalanceBefore.compareTo(
                updatedSender.getBalance().add(updatedReceiver.getBalance())));
    }

    @Test
    void rejectedTransferDoesNotChangeEitherBalance() {
        Account sender = createAccount("50.00", "500.00", "500.00");
        Account receiver = createAccount("100.00", "500.00", "500.00");

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.transfer(transfer(sender, receiver, "75.00")));

        assertAmount("50.00", getAccount(sender).getBalance());
        assertAmount("100.00", getAccount(receiver).getBalance());
    }
}
