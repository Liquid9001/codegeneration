package nl.codegeneratie.els.repository;

import nl.codegeneratie.els.domain.AtmSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtmSessionRepository extends JpaRepository<AtmSession, Long> {
}

