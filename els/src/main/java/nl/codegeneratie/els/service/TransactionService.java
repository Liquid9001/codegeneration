package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.Transaction;
import nl.codegeneratie.els.dtos.TransactionDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.TransactionRepository;
import nl.codegeneratie.els.service.helpers.TransactionHelper;
import org.springframework.beans.BeanUtils;
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
            String iban
    ) {
        int pageSize = (limit != null && limit > 0) ? limit : 50;
        int pageNumber = (offset != null && offset >= 0) ? (offset / pageSize) : 0;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("timestamp").descending());

        Page<Transaction> transactionPage = transactionRepository.findFilteredTransactions(
                startDate, endDate, minAmount, maxAmount, iban, pageable
        );

        return transactionPage.getContent().stream()
                .map(transactionHelper::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        return transactionHelper.convertToDTO(transaction);
    }

    @Transactional
    public TransactionDTO transfer(TransactionDTO dto) {
        Account sender = transactionHelper.getAccountByIban(dto.getSenderIban(), "Source");
        Account receiver = transactionHelper.getAccountByIban(dto.getReceiverIban(), "Destination");

        transactionHelper.validateTransferLimits(sender, dto.getAmount());

        sender.setBalance(sender.getBalance().subtract(dto.getAmount()));
        receiver.setBalance(receiver.getBalance().add(dto.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        return transactionHelper.createAndSaveTransactionRecord(dto, sender, receiver);
    }

    @Transactional
    public TransactionDTO deposit(TransactionDTO dto) {
        Account receiver = transactionHelper.getAccountByIban(dto.getReceiverIban(), "Destination");

        receiver.setBalance(receiver.getBalance().add(dto.getAmount()));
        accountRepository.save(receiver);

        return transactionHelper.createAndSaveTransactionRecord(dto, null, receiver);
    }

    @Transactional
    public TransactionDTO withdrawal(TransactionDTO dto) {
        Account sender = transactionHelper.getAccountByIban(dto.getSenderIban(), "Source");

        transactionHelper.validateTransferLimits(sender, dto.getAmount());

        sender.setBalance(sender.getBalance().subtract(dto.getAmount()));
        accountRepository.save(sender);

        return transactionHelper.createAndSaveTransactionRecord(dto, sender, null);
    }
}

