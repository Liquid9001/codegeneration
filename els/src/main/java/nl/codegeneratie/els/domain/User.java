package nl.codegeneratie.els.domain;

import jakarta.persistence.*;
import lombok.Data;
import nl.codegeneratie.els.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone_number")
    private Integer phoneNumber;

    @Column(unique = true)
    private Integer bsn;

    @Enumerated(EnumType.STRING)
    private UserRole role;
    private boolean approved;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;
}

