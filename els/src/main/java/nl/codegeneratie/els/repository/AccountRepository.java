package nl.codegeneratie.els.repository;

import nl.codegeneratie.els.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
	@Query("select a from Account a where a.customer_id = :customerId")
	List<Account> findByCustomerId(@Param("customerId") Long customerId);

	Optional<Account> findByIban(String iban);
}

