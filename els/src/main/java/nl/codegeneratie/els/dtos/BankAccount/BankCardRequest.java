package nl.codegeneratie.els.dtos.BankAccount;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BankCardRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String cardNumber;

    private LocalDate expiryDate;

    private int cvv;

    private Long pin;

    @ManyToOne
    private BankAccountRequest bankAccountRequest;
}
