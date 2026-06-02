package nl.codegeneratie.els.transaction;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.enums.AccountType;
import nl.codegeneratie.els.dtos.TransactionDTO;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.TransactionRepository;
import nl.codegeneratie.els.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
abstract class AbstractTransactionIntegrationTest {

    protected static final String ATM_IBAN = "NL99BANK0000000ATM1";

    @Autowired
    protected TransactionService transactionService;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected TransactionRepository transactionRepository;

    protected Account createAccount(String balance, String absoluteLimit, String dailyLimit) {
        Account account = new Account();
        account.setIban("TEST" + UUID.randomUUID().toString().replace("-", ""));
        account.setAccountType(AccountType.CHECKING);
        account.setBalance(new BigDecimal(balance));
        account.setAbsoluteTransferLimit(new BigDecimal(absoluteLimit));
        account.setDailyTransferLimit(new BigDecimal(dailyLimit));
        account.setActive(true);
        account.setCreatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    protected Account getAccount(Account account) {
        return accountRepository.findById(account.getId()).orElseThrow();
    }

    protected Account getAtm() {
        return accountRepository.findByIban(ATM_IBAN).orElseThrow();
    }

    protected TransactionDTO transfer(Account sender, Account receiver, String amount) {
        TransactionDTO dto = transaction(sender, amount, "TRANSFER");
        dto.setReceiverIban(receiver.getIban());
        return dto;
    }

    protected TransactionDTO transaction(Account sender, String amount, String type) {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderIban(sender.getIban());
        dto.setAmount(new BigDecimal(amount));
        dto.setTransactionType(type);
        return dto;
    }

    protected void assertAmount(String expected, BigDecimal actual) {
        org.junit.jupiter.api.Assertions.assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
