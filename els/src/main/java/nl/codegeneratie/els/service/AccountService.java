package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.dtos.AccountDTO;
import nl.codegeneratie.els.repository.AccountRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDTO getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        return convertToDTO(account);
    }

    public AccountDTO updateAccount(Long accountId, AccountDTO accountDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        BeanUtils.copyProperties(accountDTO, account, "id");
        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }

    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        accountRepository.delete(account);
    }

    public List<AccountDTO> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Account findByIbanOrThrow(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException("Account not found with IBAN: " + iban));
    }

    private AccountDTO convertToDTO(Account account) {
        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(account, accountDTO);
        return accountDTO;
    }
}

