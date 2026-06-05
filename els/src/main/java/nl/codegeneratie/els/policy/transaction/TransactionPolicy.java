package nl.codegeneratie.els.policy.transaction;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.Transaction;
import nl.codegeneratie.els.domain.enums.AccountType;
import nl.codegeneratie.els.exceptions.ForbiddenException;
import nl.codegeneratie.els.exceptions.InsufficientFundsException;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.TransactionRepository;
import nl.codegeneratie.els.security.SecurityUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TransactionPolicy {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionPolicy(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public void enforceValidAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }
    }

    public Long getEffectiveCustomerId(Long requestedCustomerId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (currentUserId == null) {
            return requestedCustomerId;
        }

        if (SecurityUtil.isEmployeeOrAdmin()) {
            return requestedCustomerId;
        }

        return currentUserId;
    }

    public void enforceCanViewTransaction(Transaction transaction) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (currentUserId == null || SecurityUtil.isEmployeeOrAdmin()) {
            return;
        }

        if (!transactionBelongsToUser(transaction, currentUserId)) {
            throw new ForbiddenException();
        }
    }

    public void enforceCanTransfer(Account sender, Account receiver) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (currentUserId == null || SecurityUtil.isEmployeeOrAdmin()) {
            return;
        }

        if (!accountBelongsToUser(sender, currentUserId)) {
            throw new ForbiddenException();
        }

        if (accountBelongsToUser(receiver, currentUserId)) {
            return;
        }

        if (sender.getAccountType() != AccountType.CHECKING || receiver.getAccountType() != AccountType.CHECKING) {
            throw new IllegalArgumentException("Transfers to another customer must be from a checking account to a checking account.");
        }
    }

    public void enforceCanUseAccount(Account account) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (currentUserId == null || SecurityUtil.isEmployeeOrAdmin()) {
            return;
        }

        if (!accountBelongsToUser(account, currentUserId)) {
            throw new ForbiddenException();
        }
    }

    public void enforceTransferLimits(Account sender, BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds to complete this transaction.");
        }

        if (amount.compareTo(sender.getAbsoluteTransferLimit()) > 0) {
            throw new InsufficientFundsException("Transaction amount exceeds the absolute limit of " + sender.getAbsoluteTransferLimit() + " per transaction.");
        }

        LocalDateTime startOfDay = LocalDateTime.now().with(java.time.LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(java.time.LocalTime.MAX);
        BigDecimal spentToday = transactionRepository.getTotalTransferredToday(sender.getId(), startOfDay, endOfDay);

        if (spentToday.add(amount).compareTo(sender.getDailyTransferLimit()) > 0) {
            throw new IllegalArgumentException("Transaction exceeds the daily transfer limit of " + sender.getDailyTransferLimit());
        }
    }

    public void enforceAtmHasEnoughCash(Account atm, BigDecimal amount) {
        if (atm.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("The ATM does not have enough physical cash to process this withdrawal.");
        }
    }

    private boolean transactionBelongsToUser(Transaction transaction, Long userId) {
        return accountBelongsToUser(transaction.getSenderAccountId(), userId)
                || accountBelongsToUser(transaction.getReceiverAccountId(), userId);
    }

    private boolean accountBelongsToUser(Account account, Long userId) {
        return account != null
                && account.getUser() != null
                && account.getUser().getId() != null
                && account.getUser().getId().equals(userId);
    }

    private boolean accountBelongsToUser(Long accountId, Long userId) {
        return accountId != null && accountRepository.existsByIdAndUser_Id(accountId, userId);
    }
}
