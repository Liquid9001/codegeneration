package nl.codegeneratie.els.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.DebitCard;
import nl.codegeneratie.els.domain.enums.AccountType;
import nl.codegeneratie.els.dtos.DebitCardDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.DebitCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DebitCardServiceTest {

    private DebitCardRepository debitCardRepository;
    private AccountRepository accountRepository;
    private DebitCardService debitCardService;

    private Account checkingAccount;

    @BeforeEach
    void setUp() {
        debitCardRepository = mock(DebitCardRepository.class);
        accountRepository = mock(AccountRepository.class);
        debitCardService = new DebitCardService(debitCardRepository, accountRepository);

        checkingAccount = new Account();
        checkingAccount.setId(1L);
        checkingAccount.setAccountType(AccountType.CHECKING);
    }

    @Test
    void createDebitCardCreatesFirstPublicCardNumberForCheckingAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(checkingAccount));
        when(debitCardRepository.countByAccount(checkingAccount)).thenReturn(0L);
        when(debitCardRepository.save(any(DebitCard.class))).thenAnswer(invocation -> {
            DebitCard debitCard = invocation.getArgument(0);
            debitCard.setId(10L);
            return debitCard;
        });

        DebitCardDTO result = debitCardService.createDebitCard(1L);

        assertEquals(10L, result.getId());
        assertEquals(1L, result.getAccountId());
        assertEquals(1, result.getPublicCardNumber());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void createDebitCardCreatesSecondPublicCardNumberForSameAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(checkingAccount));
        when(debitCardRepository.countByAccount(checkingAccount)).thenReturn(1L);
        when(debitCardRepository.save(any(DebitCard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DebitCardDTO result = debitCardService.createDebitCard(1L);

        assertEquals(2, result.getPublicCardNumber());
    }

    @Test
    void createDebitCardStartsAtOneForDifferentCheckingAccount() {
        Account otherCheckingAccount = new Account();
        otherCheckingAccount.setId(2L);
        otherCheckingAccount.setAccountType(AccountType.CHECKING);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(otherCheckingAccount));
        when(debitCardRepository.countByAccount(otherCheckingAccount)).thenReturn(0L);
        when(debitCardRepository.save(any(DebitCard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DebitCardDTO result = debitCardService.createDebitCard(2L);

        assertEquals(2L, result.getAccountId());
        assertEquals(1, result.getPublicCardNumber());
    }

    @Test
    void createDebitCardRejectsSavingsAccount() {
        Account savingsAccount = new Account();
        savingsAccount.setId(3L);
        savingsAccount.setAccountType(AccountType.SAVINGS);
        when(accountRepository.findById(3L)).thenReturn(Optional.of(savingsAccount));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                debitCardService.createDebitCard(3L));

        assertEquals("Debit cards can only be created for checking accounts.", exception.getMessage());
        verify(debitCardRepository, never()).save(any(DebitCard.class));
    }

    @Test
    void createDebitCardThrowsWhenAccountDoesNotExist() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> debitCardService.createDebitCard(99L));
        verify(debitCardRepository, never()).save(any(DebitCard.class));
    }

    @Test
    void createDebitCardStoresAccountAndPublicCardNumber() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(checkingAccount));
        when(debitCardRepository.countByAccount(checkingAccount)).thenReturn(0L);
        when(debitCardRepository.save(any(DebitCard.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<DebitCard> debitCardCaptor = ArgumentCaptor.forClass(DebitCard.class);

        debitCardService.createDebitCard(1L);

        verify(debitCardRepository).save(debitCardCaptor.capture());
        DebitCard savedDebitCard = debitCardCaptor.getValue();
        assertSame(checkingAccount, savedDebitCard.getAccount());
        assertEquals(1, savedDebitCard.getPublicCardNumber());
        assertNotNull(savedDebitCard.getCreatedAt());
    }

    @Test
    void debitCardDtoDoesNotExposeSensitiveOrInternalFields() throws Exception {
        DebitCardDTO debitCardDTO = new DebitCardDTO();
        debitCardDTO.setId(1L);
        debitCardDTO.setAccountId(5L);
        debitCardDTO.setPublicCardNumber(1);

        String json = new ObjectMapper().writeValueAsString(debitCardDTO);

        assertFalse(json.contains("pin"));
        assertFalse(json.contains("pinHash"));
        assertFalse(json.contains("uuid"));
        assertFalse(json.contains("account\":"));
    }
}
