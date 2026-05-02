package nl.codegeneratie.els.repository;

import nl.codegeneratie.els.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

