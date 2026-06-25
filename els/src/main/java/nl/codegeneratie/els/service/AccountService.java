package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.domain.enums.AccountType;
import nl.codegeneratie.els.domain.policies.AccountPolicy;
import nl.codegeneratie.els.dtos.AccountTransferLimitsDTO;
import nl.codegeneratie.els.dtos.AccountDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.exceptions.ForbiddenException;
import nl.codegeneratie.els.exceptions.IbanNotFoundException;
import nl.codegeneratie.els.exceptions.InvalidTransferLimitsException;
import nl.codegeneratie.els.mappers.AccountMapper;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.security.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final Random random = new Random();
    private static final String IBAN_PREFIX = "NL00ELS0";
    private static final String PIN_PATTERN = "\\d{4}";
    private final AccountMapper accountMapper;
    private final AccountPolicy accountPolicy;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper, AccountPolicy accountPolicy, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.accountPolicy = accountPolicy;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountDTO getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        accountPolicy.enforceAccountMustBeActive(account);
        return accountMapper.toAccountDTO(account);
    }

    public AccountDTO updateAccountTransferLimits(Long accountId, AccountTransferLimitsDTO limitsDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        accountPolicy.enforceTransferLimitsMustBeValid(limitsDTO);
        account.setDailyTransferLimit(limitsDTO.getDailyTransferLimit());
        account.setAbsoluteTransferLimit(limitsDTO.getAbsoluteTransferLimit());
        accountRepository.save(account);
        return accountMapper.toAccountDTO(account);
    }

    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setActive(false); // soft delete
        accountRepository.save(account);
    }

    public void createDefaultAccountsIfNeeded(User user, AccountTransferLimitsDTO checkingAccountLimitsDTO,
                                              AccountTransferLimitsDTO savingsAccountLimitsDTO) {
        validateCheckingAccountPin(checkingAccountLimitsDTO);
        accountPolicy.enforceTransferLimitsMustBeValid(checkingAccountLimitsDTO);
        accountPolicy.enforceTransferLimitsMustBeValid(savingsAccountLimitsDTO);
        // don't make two accounts if the user already has the default accounts
        accountPolicy.enforceNoDuplicateDefaultAccounts(user);
        Account checkingAccount = buildDefaultAccount(user, checkingAccountLimitsDTO, AccountType.CHECKING);
        Account savingsAccount = buildDefaultAccount(user, savingsAccountLimitsDTO, AccountType.SAVINGS);
        accountRepository.save(checkingAccount);
        accountRepository.save(savingsAccount);
    }

    private String generateIban() {
        String iban = "";
        do {
            iban = IBAN_PREFIX + String.format("%09d", random.nextInt(1_000_000_000));

        } while (accountRepository.existsByIban(iban));
        return iban;
    }

    private Account buildDefaultAccount(User user, AccountTransferLimitsDTO accountCreationDTO, AccountType accountType) {
        Account account = new Account();
        account.setUser(user);
        account.setIban(generateIban());
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setAbsoluteTransferLimit(accountCreationDTO.getAbsoluteTransferLimit());
        account.setDailyTransferLimit(accountCreationDTO.getDailyTransferLimit());
        if (accountType == AccountType.CHECKING) {
            account.setPinHash(passwordEncoder.encode(accountCreationDTO.getPin()));
        }
        account.setActive(true);
        account.setCreatedAt(LocalDateTime.now());
        return account;
    }

    private void validateCheckingAccountPin(AccountTransferLimitsDTO checkingAccountLimitsDTO) {
        if (checkingAccountLimitsDTO == null || checkingAccountLimitsDTO.getPin() == null || checkingAccountLimitsDTO.getPin().isBlank()) {
            throw new IllegalArgumentException("PIN is required for checking account creation.");
        }

        if (!checkingAccountLimitsDTO.getPin().matches(PIN_PATTERN)) {
            throw new IllegalArgumentException("PIN must be exactly 4 digits.");
        }
    }
}

