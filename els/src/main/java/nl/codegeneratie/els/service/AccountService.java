package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.domain.enums.AccountType;
import nl.codegeneratie.els.dtos.AccountTransferLimitsDTO;
import nl.codegeneratie.els.dtos.AccountDTO;
import nl.codegeneratie.els.exceptions.AccountNotFoundException;
import nl.codegeneratie.els.exceptions.ForbiddenException;
import nl.codegeneratie.els.exceptions.IbanNotFoundException;
import nl.codegeneratie.els.exceptions.InvalidTransferLimitsException;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.security.SecurityUtil;
import org.springframework.beans.BeanUtils;
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

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDTO getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));
        if (!account.isActive()) {
            throw new AccountNotFoundException(accountId);
        }
        Long currentUserId = SecurityUtil.getCurrentUserId();
        Long ownerId = account.getUser().getId();
        if (!SecurityUtil.isEmployeeOrAdmin() && !ownerId.equals(currentUserId)) {
            throw new ForbiddenException();
        }
        return convertToDTO(account);
    }

    public AccountDTO updateAccountTransferLimits(Long accountId, AccountTransferLimitsDTO limitsDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        validateLimits(limitsDTO);
        account.setDailyTransferLimit(limitsDTO.getDailyTransferLimit());
        account.setAbsoluteTransferLimit(limitsDTO.getAbsoluteTransferLimit());
        accountRepository.save(account);
        return convertToDTO(account);
    }

    private boolean areTransferLimitsValid(AccountTransferLimitsDTO limitsDTO) {
        BigDecimal dailyTransferLimit = limitsDTO.getDailyTransferLimit();
        BigDecimal absoluteTransferLimit = limitsDTO.getAbsoluteTransferLimit();
        // Daily limit has to be higher than absolute limit, and both need to be higher than zero
        return dailyTransferLimit.compareTo(absoluteTransferLimit) >= 0 && dailyTransferLimit.compareTo(BigDecimal.ZERO) > 0;
    }

    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.setActive(false); // soft delete
        accountRepository.save(account);
    }

    public List<AccountDTO> getAccountsByUser_Id(Long userId) {
        return accountRepository.findByUser_Id(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Account findByIbanOrThrow(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new IbanNotFoundException(iban));
    }

    public void createDefaultAccountsIfNeeded(User user, AccountTransferLimitsDTO checkingAccountLimitsDTO,
                                              AccountTransferLimitsDTO savingsAccountLimitsDTO) {
        validateLimits(checkingAccountLimitsDTO);
        validateLimits(savingsAccountLimitsDTO);
        List<Account> existing = accountRepository.findByUser(user);
        if (existing.isEmpty()) {
            Account checkingAccount = buildDefaultAccount(user, checkingAccountLimitsDTO, AccountType.CHECKING);
            Account savingsAccount = buildDefaultAccount(user, savingsAccountLimitsDTO, AccountType.SAVINGS);
            accountRepository.save(checkingAccount);
            accountRepository.save(savingsAccount);
        }
    }

    private void validateLimits(AccountTransferLimitsDTO dto) {
        if (!areTransferLimitsValid(dto)) {
            throw new InvalidTransferLimitsException(dto.getDailyTransferLimit(), dto.getAbsoluteTransferLimit());
        }
    }

    private AccountDTO convertToDTO(Account account) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setFirstName(account.getUser().getFirstName());
        accountDTO.setLastName(account.getUser().getLastName());
        BeanUtils.copyProperties(account, accountDTO);
        return accountDTO;
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
        account.setActive(true);
        account.setCreatedAt(LocalDateTime.now());
        return account;
    }
}

