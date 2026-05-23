package nl.codegeneratie.els.domain;

import jakarta.persistence.*;
import lombok.Data;

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

    private String password_hash;
    private String first_name;
    private String last_name;
    private Integer phone_number;

    @Column(unique = true)
    private Integer bsn;

    private Integer role;
    private boolean approved;
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;
}

