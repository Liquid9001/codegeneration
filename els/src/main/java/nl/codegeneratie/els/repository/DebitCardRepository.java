package nl.codegeneratie.els.repository;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.DebitCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebitCardRepository extends JpaRepository<DebitCard, Long> {
    long countByAccount(Account account);
}
