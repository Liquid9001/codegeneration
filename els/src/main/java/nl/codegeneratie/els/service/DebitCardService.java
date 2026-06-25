package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.DebitCard;
import nl.codegeneratie.els.domain.enums.AccountType;
import nl.codegeneratie.els.dtos.DebitCardDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.DebitCardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DebitCardService {

    private final DebitCardRepository debitCardRepository;
    private final AccountRepository accountRepository;

    public DebitCardService(DebitCardRepository debitCardRepository, AccountRepository accountRepository) {
        this.debitCardRepository = debitCardRepository;
        this.accountRepository = accountRepository;
    }

    public DebitCardDTO createDebitCard(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        if (account.getAccountType() != AccountType.CHECKING) {
            throw new IllegalArgumentException("Debit cards can only be created for checking accounts.");
        }

        DebitCard debitCard = new DebitCard();
        debitCard.setAccount(account);
        debitCard.setPublicCardNumber(nextPublicCardNumber(account));
        debitCard.setCreatedAt(LocalDateTime.now());

        DebitCard savedDebitCard = debitCardRepository.save(debitCard);
        return returnDebitCardToDTO(savedDebitCard);
    }

    private int nextPublicCardNumber(Account account) {
        return (int)(debitCardRepository.countByAccount(account) + 1);
    }

    private DebitCardDTO returnDebitCardToDTO(DebitCard debitCard) {
        DebitCardDTO debitCardDTO = new DebitCardDTO();
        debitCardDTO.setId(debitCard.getId());
        debitCardDTO.setAccountId(debitCard.getAccount().getId());
        debitCardDTO.setPublicCardNumber(debitCard.getPublicCardNumber());
        debitCardDTO.setCreatedAt(debitCard.getCreatedAt());
        return debitCardDTO;
    }
}
