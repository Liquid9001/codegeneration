package nl.codegeneratie.els.repository;


import nl.codegeneratie.els.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Transaction, Long> {
}
