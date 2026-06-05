package nl.codegeneratie.els.mappers;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.Transaction;
import nl.codegeneratie.els.dtos.TransactionDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    public Transaction toTransaction(TransactionDTO dto, Account sender, Account receiver) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(dto, transaction, "senderIban", "receiverIban");

        if (sender != null) {
            transaction.setSenderAccountId(sender.getId());
            transaction.setSenderIban(sender.getIban());
        }

        if (receiver != null) {
            transaction.setReceiverAccountId(receiver.getId());
            transaction.setReceiverIban(receiver.getIban());
        }

        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(LocalDateTime.now());
        }

        if (transaction.getCurrency() == null) {
            transaction.setCurrency("EUR");
        }

        if (transaction.getStatus() == null) {
            transaction.setStatus("COMPLETED");
        }

        return transaction;
    }

    public TransactionDTO toTransactionDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        BeanUtils.copyProperties(transaction, transactionDTO);
        return transactionDTO;
    }
}
