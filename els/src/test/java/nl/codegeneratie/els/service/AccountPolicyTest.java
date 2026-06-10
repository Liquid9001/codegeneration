package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.dtos.AccountTransferLimitsDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.exceptions.AccountsAlreadyExistException;
import nl.codegeneratie.els.exceptions.InvalidTransferLimitsException;
import nl.codegeneratie.els.domain.policies.AccountPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountPolicyTest {

    private AccountPolicy accountPolicy;

    @BeforeEach
    void setUp() {
        accountPolicy = new AccountPolicy();
    }

    @Test
    void enforceTransferLimitsMustBeValid_shouldNotThrow_whenValid() {
        AccountTransferLimitsDTO dto = new AccountTransferLimitsDTO();
        dto.setDailyTransferLimit(BigDecimal.valueOf(1000));
        dto.setAbsoluteTransferLimit(BigDecimal.valueOf(5000));
        assertDoesNotThrow(() -> accountPolicy.enforceTransferLimitsMustBeValid(dto));
    }

    @Test
    void enforceTransferLimitsMustBeValid_shouldThrow_whenDailyLimitZero() {
        AccountTransferLimitsDTO dto = new AccountTransferLimitsDTO();
        dto.setDailyTransferLimit(BigDecimal.ZERO);
        dto.setAbsoluteTransferLimit(BigDecimal.valueOf(5000));
        assertThrows(InvalidTransferLimitsException.class, () -> accountPolicy.enforceTransferLimitsMustBeValid(dto));
    }

    @Test
    void enforceTransferLimitsMustBeValid_shouldThrow_whenAbsoluteLimitZero() {
        AccountTransferLimitsDTO dto = new AccountTransferLimitsDTO();
        dto.setDailyTransferLimit(BigDecimal.valueOf(1000));
        dto.setAbsoluteTransferLimit(BigDecimal.ZERO);
        assertThrows(InvalidTransferLimitsException.class, () -> accountPolicy.enforceTransferLimitsMustBeValid(dto));
    }

    @Test
    void enforceAccountMustBeActive_shouldNotThrow_whenActive() {
        Account account = new Account();
        account.setId(1L);
        account.setActive(true);
        assertDoesNotThrow(() -> accountPolicy.enforceAccountMustBeActive(account));
    }

    @Test
    void enforceAccountMustBeActive_shouldThrow_whenInactive() {
        Account account = new Account();
        account.setId(1L);
        account.setActive(false);
        assertThrows(AccountNotFoundException.class, () -> accountPolicy.enforceAccountMustBeActive(account));
    }

    @Test
    void enforceNoDuplicateDefaultAccounts_shouldNotThrow_whenNoAccounts() {
        User user = new User();
        user.setAccounts(List.of());
        assertDoesNotThrow(() -> accountPolicy.enforceNoDuplicateDefaultAccounts(user));
    }

    @Test
    void enforceNoDuplicateDefaultAccounts_shouldThrow_whenUserHasAccounts() {
        User user = new User();
        user.setAccounts(List.of(new Account()));
        assertThrows(AccountsAlreadyExistException.class, () -> accountPolicy.enforceNoDuplicateDefaultAccounts(user));
    }
}