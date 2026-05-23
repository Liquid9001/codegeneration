package nl.codegeneratie.els.repository;

import nl.codegeneratie.els.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

