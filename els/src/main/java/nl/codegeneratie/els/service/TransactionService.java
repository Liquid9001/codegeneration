package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.Transaction;
import nl.codegeneratie.els.domain.enums.AccountType;
import nl.codegeneratie.els.dtos.TransactionDTO;
import nl.codegeneratie.els.exceptions.ForbiddenException;
import nl.codegeneratie.els.exceptions.TransactionNotFoundException;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.TransactionRepository;
import nl.codegeneratie.els.security.SecurityUtil;
import nl.codegeneratie.els.service.helpers.TransactionHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionHelper transactionHelper;
    private static final String ATM_IBAN = "NL99BANK0000000ATM1";

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, TransactionHelper transactionHelper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionHelper = transactionHelper;
    }

    public List<TransactionDTO> getAllTransactions(
            Integer offset,
            Integer limit,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String iban,
            String transactionType,
            Long customerId
    ) {//look at request data, return page object, cleaner way to handle pagination data
        int pageSize = (limit != null && limit > 0) ? limit : 50;
        int pageNumber = (offset != null && offset >= 0) ? (offset / pageSize) : 0;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("timestamp").descending());
        Long effectiveCustomerId = getEffectiveCustomerId(customerId);

        Page<Transaction> transactionPage = transactionRepository.findFilteredTransactions(
                startDate, endDate, minAmount, maxAmount, iban, normalize(transactionType), effectiveCustomerId, pageable
        );

        return transactionPage.getContent().stream()
                .map(transactionHelper::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        validateTransactionAccess(transaction);
        return transactionHelper.convertToDTO(transaction);
    }

    @Transactional
    public TransactionDTO transfer(TransactionDTO dto) {
        validateAmount(dto);
        applyCurrentUserInitiator(dto);
        Account sender = transactionHelper.getAccountByIban(dto.getSenderIban(), "Source");
        Account receiver = transactionHelper.getAccountByIban(dto.getReceiverIban(), "Destination");
        validateCustomerTransferAccess(sender, receiver);

        transactionHelper.validateTransferLimits(sender, dto.getAmount());

        sender.setBalance(sender.getBalance().subtract(dto.getAmount()));
        receiver.setBalance(receiver.getBalance().add(dto.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        return transactionHelper.createAndSaveTransactionRecord(dto, sender, receiver);
    }

    @Transactional
    public TransactionDTO deposit(TransactionDTO dto) {
        validateAmount(dto);
        applyCurrentUserInitiator(dto);
        Account receiver = transactionHelper.getAccountByIban(dto.getReceiverIban(), "Destination");
        Account atm = transactionHelper.getAccountByIban(ATM_IBAN, "ATM System");
        validateCustomerAccountAccess(receiver);

        receiver.setBalance(receiver.getBalance().add(dto.getAmount()));
        atm.setBalance(atm.getBalance().add(dto.getAmount()));

        accountRepository.save(receiver);
        accountRepository.save(atm);

        return transactionHelper.createAndSaveTransactionRecord(dto, null, receiver);
    }

    @Transactional
    public TransactionDTO withdrawal(TransactionDTO dto) {
        validateAmount(dto);
        applyCurrentUserInitiator(dto);
        Account sender = transactionHelper.getAccountByIban(dto.getSenderIban(), "Source");
        Account atm = transactionHelper.getAccountByIban(ATM_IBAN, "ATM System");
        validateCustomerAccountAccess(sender);

        transactionHelper.validateTransferLimits(sender, dto.getAmount());

        if (atm.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new IllegalArgumentException("The ATM does not have enough physical cash to process this withdrawal.");
        }

        sender.setBalance(sender.getBalance().subtract(dto.getAmount()));
        atm.setBalance(atm.getBalance().subtract(dto.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(atm);

        return transactionHelper.createAndSaveTransactionRecord(dto, sender, null);
    }

    private Long getEffectiveCustomerId(Long requestedCustomerId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (currentUserId == null) {
            return requestedCustomerId;
        }

        if (SecurityUtil.isEmployeeOrAdmin()) {
            return requestedCustomerId;
        }

        return currentUserId;
    }

    private void validateTransactionAccess(Transaction transaction) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (currentUserId == null || SecurityUtil.isEmployeeOrAdmin()) {
            return;
        }

        if (!transactionBelongsToUser(transaction, currentUserId)) {
            throw new ForbiddenException();
        }
    }

    private boolean transactionBelongsToUser(Transaction transaction, Long userId) {
        return accountBelongsToUser(transaction.getSenderAccountId(), userId)
                || accountBelongsToUser(transaction.getReceiverAccountId(), userId);
    }

    private void validateCustomerTransferAccess(Account sender, Account receiver) {
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

    private void validateCustomerAccountAccess(Account account) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (currentUserId == null || SecurityUtil.isEmployeeOrAdmin()) {
            return;
        }

        if (!accountBelongsToUser(account, currentUserId)) {
            throw new ForbiddenException();
        }
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

    private void validateAmount(TransactionDTO dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }
    }

    private void applyCurrentUserInitiator(TransactionDTO dto) {
        if (dto.getInitiatedByUserId() != null) {
            return;
        }

        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId != null) {
            dto.setInitiatedByUserId(currentUserId);
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase();
    }
}

