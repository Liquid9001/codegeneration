package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.domain.enums.AccountType;
import nl.codegeneratie.els.dtos.AccountDTO;
import nl.codegeneratie.els.dtos.AccountTransferLimitsDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.exceptions.AccountsAlreadyExistException;
import nl.codegeneratie.els.exceptions.InvalidTransferLimitsException;
import nl.codegeneratie.els.mappers.AccountMapper;
import nl.codegeneratie.els.domain.policies.AccountPolicy;
import nl.codegeneratie.els.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    private AccountService accountService;
    private PasswordEncoder passwordEncoder;

    private Account account;
    private AccountDTO accountDTO;
    private AccountTransferLimitsDTO limitsDTO;
    private AccountTransferLimitsDTO checkingDto;
    private AccountTransferLimitsDTO savingsDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        accountService = new AccountService(accountRepository, accountMapper, accountPolicy, passwordEncoder);

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

        checkingDto = new AccountTransferLimitsDTO();
        checkingDto.setDailyTransferLimit(BigDecimal.valueOf(5000));
        checkingDto.setAbsoluteTransferLimit(BigDecimal.valueOf(1000));
        checkingDto.setPin("1234");

        savingsDto = new AccountTransferLimitsDTO();
        savingsDto.setDailyTransferLimit(BigDecimal.valueOf(10000));
        savingsDto.setAbsoluteTransferLimit(BigDecimal.valueOf(5000));
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
    void createDefaultAccountsThrowsWhenAccountsAlreadyExist() {
        User user = new User();
        user.setAccounts(List.of(new Account()));
        doThrow(new AccountsAlreadyExistException(user)).when(accountPolicy).enforceNoDuplicateDefaultAccounts(user);
        assertThrows(AccountsAlreadyExistException.class, () ->
                accountService.createDefaultAccountsIfNeeded(user, checkingDto, savingsDto));
        verify(accountPolicy).enforceNoDuplicateDefaultAccounts(user);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createDefaultAccountsCreatesCheckingAndSavings() {
        User user = new User();
        user.setAccounts(new ArrayList<>());
        accountService.createDefaultAccountsIfNeeded(user, checkingDto, savingsDto);
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void createDefaultAccountsStoresHashedPinForCheckingOnly() {
        User user = new User();
        user.setAccounts(new ArrayList<>());
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        accountService.createDefaultAccountsIfNeeded(user, checkingDto, savingsDto);

        verify(accountRepository, times(2)).save(accountCaptor.capture());
        Account checkingAccount = accountCaptor.getAllValues().stream()
                .filter(savedAccount -> savedAccount.getAccountType() == AccountType.CHECKING)
                .findFirst()
                .orElseThrow();
        Account savingsAccount = accountCaptor.getAllValues().stream()
                .filter(savedAccount -> savedAccount.getAccountType() == AccountType.SAVINGS)
                .findFirst()
                .orElseThrow();

        assertNotNull(checkingAccount.getPinHash());
        assertNotEquals("1234", checkingAccount.getPinHash());
        assertTrue(passwordEncoder.matches("1234", checkingAccount.getPinHash()));
        assertNull(savingsAccount.getPinHash());
    }

    @Test
    void createDefaultAccountsRejectsMissingCheckingPin() {
        User user = new User();
        user.setAccounts(new ArrayList<>());
        checkingDto.setPin(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                accountService.createDefaultAccountsIfNeeded(user, checkingDto, savingsDto));

        assertEquals("PIN is required for checking account creation.", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createDefaultAccountsRejectsInvalidCheckingPin() {
        User user = new User();
        user.setAccounts(new ArrayList<>());

        for (String invalidPin : List.of("12a4", "123", "12345")) {
            checkingDto.setPin(invalidPin);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    accountService.createDefaultAccountsIfNeeded(user, checkingDto, savingsDto));
            assertEquals("PIN must be exactly 4 digits.", exception.getMessage());
        }

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void accountDtoDoesNotExposePinOrPinHash() {
        assertThrows(NoSuchFieldException.class, () -> AccountDTO.class.getDeclaredField("pin"));
        assertThrows(NoSuchFieldException.class, () -> AccountDTO.class.getDeclaredField("pinHash"));
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
