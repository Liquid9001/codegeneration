package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Transaction;
import nl.codegeneratie.els.dtos.TransactionDTO;
import nl.codegeneratie.els.repository.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(transactionDTO, transaction);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }

    public TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        BeanUtils.copyProperties(transactionDTO, transaction, "id");
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToDTO(updatedTransaction);
    }

    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        transactionRepository.delete(transaction);
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        BeanUtils.copyProperties(transaction, transactionDTO);
        return transactionDTO;
    }
}

