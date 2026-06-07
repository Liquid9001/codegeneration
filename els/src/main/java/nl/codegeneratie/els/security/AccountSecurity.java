package nl.codegeneratie.els.security;

import nl.codegeneratie.els.repository.AccountRepository;
import org.springframework.stereotype.Component;

@Component
public class AccountSecurity {
    private final AccountRepository accountRepository;
    public AccountSecurity(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public boolean isOwner(Long accountId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return accountRepository.existsByIdAndUser_Id(accountId, currentUserId);
    }
}
