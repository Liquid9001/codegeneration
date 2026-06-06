package nl.codegeneratie.els.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.codegeneratie.els.domain.enums.UserAccountStatus;
import nl.codegeneratie.els.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "users")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserAccountStatus userAccountStatus = UserAccountStatus.PENDING;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;
}

