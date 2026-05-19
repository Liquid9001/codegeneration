package nl.codegeneratie.els.repository;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findByUser(User user);
	Optional<Account> findByIban(String iban);
}

