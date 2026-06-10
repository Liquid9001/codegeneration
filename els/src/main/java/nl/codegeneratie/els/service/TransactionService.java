package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.Transaction;
import nl.codegeneratie.els.dtos.TransactionDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.exceptions.TransactionNotFoundException;
import nl.codegeneratie.els.mappers.TransactionMapper;
import nl.codegeneratie.els.policy.transaction.TransactionPolicy;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.TransactionRepository;
import nl.codegeneratie.els.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionPolicy transactionPolicy;
    private final TransactionMapper transactionMapper;
    private static final String ATM_IBAN = "NL99BANK0000000ATM1";

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, TransactionPolicy transactionPolicy, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionPolicy = transactionPolicy;
        this.transactionMapper = transactionMapper;
    }

    public Page<TransactionDTO> getAllTransactions(
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String iban,
            String transactionType,
            Long customerId,
            Pageable pageable
    ) {
        Long effectiveCustomerId = transactionPolicy.getEffectiveCustomerId(customerId);

        return transactionRepository.findFilteredTransactions(
                startDate, endDate, minAmount, maxAmount, iban, normalize(transactionType), effectiveCustomerId, pageable
        ).map(transactionMapper::toTransactionDTO);
    }

    public TransactionDTO getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        transactionPolicy.enforceCanViewTransaction(transaction);
        return transactionMapper.toTransactionDTO(transaction);
    }

    @Transactional
    public TransactionDTO transfer(TransactionDTO dto) {
        transactionPolicy.enforceValidAmount(dto.getAmount());
        applyCurrentUserInitiator(dto);
        Account sender = getAccountByIban(dto.getSenderIban(), "Source");
        Account receiver = getAccountByIban(dto.getReceiverIban(), "Destination");
        transactionPolicy.enforceCanTransfer(sender, receiver);

        transactionPolicy.enforceTransferLimits(sender, dto.getAmount());

        sender.setBalance(sender.getBalance().subtract(dto.getAmount()));
        receiver.setBalance(receiver.getBalance().add(dto.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        return saveTransactionRecord(dto, sender, receiver);
    }

    @Transactional
    public TransactionDTO deposit(TransactionDTO dto) {
        transactionPolicy.enforceValidAmount(dto.getAmount());
        applyCurrentUserInitiator(dto);
        Account receiver = getAccountByIban(dto.getReceiverIban(), "Destination");
        Account atm = getAccountByIban(ATM_IBAN, "ATM System");
        transactionPolicy.enforceCanUseAccount(receiver);

        receiver.setBalance(receiver.getBalance().add(dto.getAmount()));
        atm.setBalance(atm.getBalance().add(dto.getAmount()));

        accountRepository.save(receiver);
        accountRepository.save(atm);

        return saveTransactionRecord(dto, null, receiver);
    }

    @Transactional
    public TransactionDTO withdrawal(TransactionDTO dto) {
        transactionPolicy.enforceValidAmount(dto.getAmount());
        applyCurrentUserInitiator(dto);
        Account sender = getAccountByIban(dto.getSenderIban(), "Source");
        Account atm = getAccountByIban(ATM_IBAN, "ATM System");
        transactionPolicy.enforceCanUseAccount(sender);

        transactionPolicy.enforceTransferLimits(sender, dto.getAmount());
        transactionPolicy.enforceAtmHasEnoughCash(atm, dto.getAmount());

        sender.setBalance(sender.getBalance().subtract(dto.getAmount()));
        atm.setBalance(atm.getBalance().subtract(dto.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(atm);

        return saveTransactionRecord(dto, sender, null);
    }

    private TransactionDTO saveTransactionRecord(TransactionDTO dto, Account sender, Account receiver) {
        Transaction transaction = transactionMapper.toTransaction(dto, sender, receiver);
        return transactionMapper.toTransactionDTO(transactionRepository.save(transaction));
    }

    private Account getAccountByIban(String iban, String type) {
        if (iban == null || iban.trim().isEmpty()) {
            throw new IllegalArgumentException(type + " IBAN is required for this transaction type.");
        }

        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException(type + " account not found for IBAN: " + iban));
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
