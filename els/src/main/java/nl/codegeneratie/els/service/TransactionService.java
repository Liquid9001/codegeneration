package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.Transaction;
import nl.codegeneratie.els.dtos.TransactionDTO;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<TransactionDTO> getAllTransactions(
            Integer offset,
            Integer limit,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Double minAmount,
            Double maxAmount,
            String iban
    ) {
        List<TransactionDTO> all = transactionRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());

        List<TransactionDTO> filtered = all.stream()
                .filter(t -> startDate == null || (t.getTimestamp() != null && !t.getTimestamp().isBefore(startDate)))
                .filter(t -> endDate == null || (t.getTimestamp() != null && !t.getTimestamp().isAfter(endDate)))
                .filter(t -> minAmount == null || (t.getAmount() != null && t.getAmount().doubleValue() >= minAmount))
                .filter(t -> maxAmount == null || (t.getAmount() != null && t.getAmount().doubleValue() <= maxAmount))
                .filter(t -> iban == null || Objects.equals(t.getFrom_iban(), iban) || Objects.equals(t.getTo_iban(), iban))
                .collect(Collectors.toList());

        int safeOffset = offset == null ? 0 : Math.max(offset, 0);
        int safeLimit = limit == null ? filtered.size() : Math.max(limit, 1);
        int end = Math.min(safeOffset + safeLimit, filtered.size());
        if (safeOffset >= filtered.size()) {
            return List.of();
        }
        return filtered.subList(safeOffset, end);
    }

    public TransactionDTO getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        return convertToDTO(transaction);
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Account from = accountRepository.findByIban(transactionDTO.getFrom_iban())
                .orElseThrow(() -> new RuntimeException("Source account not found for IBAN: " + transactionDTO.getFrom_iban()));
        Account to = accountRepository.findByIban(transactionDTO.getTo_iban())
                .orElseThrow(() -> new RuntimeException("Destination account not found for IBAN: " + transactionDTO.getTo_iban()));

        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(transactionDTO, transaction, "from_iban", "to_iban");
        transaction.setFrom_account_id(from.getId());
        transaction.setTo_account_id(to.getId());
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(LocalDateTime.now());
        }
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        BeanUtils.copyProperties(transaction, transactionDTO, "from_iban", "to_iban");
        accountRepository.findById(transaction.getFrom_account_id()).ifPresent(a -> transactionDTO.setFrom_iban(a.getIban()));
        accountRepository.findById(transaction.getTo_account_id()).ifPresent(a -> transactionDTO.setTo_iban(a.getIban()));
        return transactionDTO;
    }
}

