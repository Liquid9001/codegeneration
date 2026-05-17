package nl.codegeneratie.els.repository;

import nl.codegeneratie.els.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	@Query("select u from User u where u.first_name = :firstName and u.last_name = :lastName")
	List<User> findByFirstAndLastName(
			@Param("firstName") String firstName,
			@Param("lastName") String lastName
	);
}

