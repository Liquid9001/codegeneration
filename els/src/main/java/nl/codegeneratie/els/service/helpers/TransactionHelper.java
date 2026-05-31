package nl.codegeneratie.els.service.helpers;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.Transaction;
import nl.codegeneratie.els.dtos.TransactionDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.exceptions.InsufficientFundsException;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TransactionHelper {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;

	public TransactionHelper(AccountRepository accountRepository, TransactionRepository transactionRepository) {
		this.accountRepository = accountRepository;
		this.transactionRepository = transactionRepository;
	}

	public Account getAccountByIban(String iban, String type) {
		if (iban == null || iban.trim().isEmpty()) {
			throw new IllegalArgumentException(type + " IBAN is required for this transaction type.");
		}

		return accountRepository.findByIban(iban)
				.orElseThrow(() -> new AccountNotFoundException(type + " account not found for IBAN: " + iban));
	}

	public void validateTransferLimits(Account sender, BigDecimal amount) {
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

	public TransactionDTO createAndSaveTransactionRecord(TransactionDTO dto, Account sender, Account receiver) {
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

		return convertToDTO(transactionRepository.save(transaction));
	}

	public TransactionDTO convertToDTO(Transaction transaction) {
		TransactionDTO transactionDTO = new TransactionDTO();
		BeanUtils.copyProperties(transaction, transactionDTO);
		return transactionDTO;
	}

}
