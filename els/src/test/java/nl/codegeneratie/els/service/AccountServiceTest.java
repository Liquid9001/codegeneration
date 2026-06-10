package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.domain.enums.AccountType;
import nl.codegeneratie.els.dtos.AccountDTO;
import nl.codegeneratie.els.dtos.AccountTransferLimitsDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.exceptions.InvalidTransferLimitsException;
import nl.codegeneratie.els.mappers.AccountMapper;
import nl.codegeneratie.els.domain.policies.AccountPolicy;
import nl.codegeneratie.els.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountPolicy accountPolicy;

    @InjectMocks
    private AccountService accountService;

    private Account account;
    private AccountDTO accountDTO;
    private AccountTransferLimitsDTO limitsDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        account = new Account();
        account.setId(1L);
        account.setAccountType(AccountType.CHECKING);
        account.setBalance(BigDecimal.valueOf(1000));
        account.setActive(true);

        accountDTO = new AccountDTO();
        accountDTO.setId(1L);

        limitsDTO = new AccountTransferLimitsDTO();
        limitsDTO.setDailyTransferLimit(BigDecimal.valueOf(5000));
        limitsDTO.setAbsoluteTransferLimit(BigDecimal.valueOf(10000));
    }

    @Test
    void getAccountById_ShouldReturnAccountDTO_WhenAccountExists() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        doNothing().when(accountPolicy).enforceAccountMustBeActive(account);
        when(accountMapper.toAccountDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountService.getAccountById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(accountRepository).findById(1L);
        verify(accountPolicy).enforceAccountMustBeActive(account);
    }

    @Test
    void getAccountById_ShouldThrow_WhenAccountDoesNotExist() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(99L));
        verify(accountRepository).findById(99L);
    }

    @Test
    void updateAccountTransferLimits_ShouldUpdateLimits_WhenValidLimits() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        doNothing().when(accountPolicy).enforceTransferLimitsMustBeValid(limitsDTO);
        when(accountMapper.toAccountDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountService.updateAccountTransferLimits(1L, limitsDTO);

        assertNotNull(result);
        verify(accountPolicy).enforceTransferLimitsMustBeValid(limitsDTO);
        verify(accountRepository).save(account);
    }

    @Test
    void updateAccountTransferLimits_ShouldThrow_WhenInvalidLimits() {
        AccountTransferLimitsDTO invalidLimits = new AccountTransferLimitsDTO();
        invalidLimits.setDailyTransferLimit(BigDecimal.ZERO);
        invalidLimits.setAbsoluteTransferLimit(BigDecimal.ZERO);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        doThrow(new InvalidTransferLimitsException(BigDecimal.ZERO, BigDecimal.ZERO))
                .when(accountPolicy).enforceTransferLimitsMustBeValid(invalidLimits);

        assertThrows(InvalidTransferLimitsException.class, () ->
                accountService.updateAccountTransferLimits(1L, invalidLimits)
        );
        verify(accountRepository, never()).save(account);
    }

    @Test
    void deleteAccount_ShouldDeactivateAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.deleteAccount(1L);

        assertFalse(account.isActive());
        verify(accountRepository).save(account);
    }
}