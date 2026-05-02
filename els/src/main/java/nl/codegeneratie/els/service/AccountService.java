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

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = new Account();
        BeanUtils.copyProperties(accountDTO, account);
        Account savedAccount = accountRepository.save(account);
        return convertToDTO(savedAccount);
    }

    public AccountDTO updateAccount(Long id, AccountDTO accountDTO) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        BeanUtils.copyProperties(accountDTO, account, "id");
        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        accountRepository.delete(account);
    }

    private AccountDTO convertToDTO(Account account) {
        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(account, accountDTO);
        return accountDTO;
    }
}

