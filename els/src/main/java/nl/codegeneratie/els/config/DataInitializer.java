package nl.codegeneratie.els.config;

import nl.codegeneratie.els.domain.*;
import nl.codegeneratie.els.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository userRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Create Users
            User user1 = new User();
            user1.setEmail("john.doe@example.com");
            user1.setPasswordHash(passwordEncoder.encode("password123"));
            user1.setFirstName("John");
            user1.setLastName("Doe");
            user1.setPhoneNumber(612345678);
            user1.setBsn(123456789);
            user1.setRole(0);
            user1.setApproved(true);
            user1.setCreatedAt(
                    LocalDateTime.parse("2024-01-15 10:30:00", formatter)
            );
            userRepository.save(user1);

            User user2 = new User();
            user2.setEmail("jane.smith@example.com");
            user2.setPasswordHash(passwordEncoder.encode("password123"));
            user2.setFirstName("Jane");
            user2.setLastName("Smith");
            user2.setPhoneNumber(621234567);
            user2.setBsn(987654321);
            user2.setRole(0);
            user2.setApproved(true);
            user2.setCreatedAt(
                    LocalDateTime.parse("2024-01-16 14:45:00", formatter)
            );
            userRepository.save(user2);

            User user3 = new User();
            user3.setEmail("michael.johnson@example.com");
            user3.setPasswordHash(passwordEncoder.encode("password123"));
            user3.setFirstName("Michael");
            user3.setLastName("Johnson");
            user3.setPhoneNumber(698765432);
            user3.setBsn(555666777);
            user3.setRole(0);
            user3.setApproved(true);
            user3.setCreatedAt(
                    LocalDateTime.parse("2024-01-17 09:15:00", formatter)
            );
            userRepository.save(user3);

            User user4 = new User();
            user4.setEmail("sara.williams@example.com");
            user4.setPasswordHash(passwordEncoder.encode("password123"));
            user4.setFirstName("Sara");
            user4.setLastName("Williams");
            user4.setPhoneNumber(643210987);
            user4.setBsn(111222333);
            user4.setRole(1);
            user4.setApproved(true);
            user4.setCreatedAt(
                    LocalDateTime.parse("2024-01-18 11:30:00", formatter)
            );
            userRepository.save(user4);

            User user5 = new User();
            user5.setEmail("admin@example.com");
            user5.setPasswordHash(passwordEncoder.encode("admin123"));
            user5.setFirstName("Admin");
            user5.setLastName("User");
            user5.setPhoneNumber(600000000);
            user5.setBsn(999999999);
            user5.setRole(2);
            user5.setApproved(true);
            user5.setCreatedAt(
                    LocalDateTime.parse("2024-01-01 08:00:00", formatter)
            );
            userRepository.save(user5);

            // Create Accounts
            Account account1 = new Account();
            account1.setUser(user1);
            account1.setIban("NL91ABNA0417164300");
            account1.setAccountType("CHECKING");
            account1.setBalance(new BigDecimal("5000.00"));
            account1.setAbsoluteTransferLimit(new BigDecimal("10000.00"));
            account1.setDailyTransferLimit(new BigDecimal("20000.00"));
            account1.setActive(true);
            account1.setCreatedAt(LocalDateTime.parse("2024-01-15 10:30:00", formatter));
            accountRepository.save(account1);

            Account account2 = new Account();
            account2.setUser(user1);
            account2.setIban("NL47ABNA0123456789");
            account2.setAccountType("SAVINGS");
            account2.setBalance(new BigDecimal("15000.00"));
            account2.setAbsoluteTransferLimit(new BigDecimal("5000.00"));
            account2.setDailyTransferLimit(new BigDecimal("10000.00"));
            account2.setActive(true);
            account2.setCreatedAt(LocalDateTime.parse("2024-02-01 14:20:00", formatter));
            accountRepository.save(account2);

            Account account3 = new Account();
            account3.setUser(user2);
            account3.setIban("NL61ABNA0123456790");
            account3.setAccountType("CHECKING");
            account3.setBalance(new BigDecimal("8500.50"));
            account3.setAbsoluteTransferLimit(new BigDecimal("10000.00"));
            account3.setDailyTransferLimit(new BigDecimal("20000.00"));
            account3.setActive(true);
            account3.setCreatedAt(LocalDateTime.parse("2024-01-16 14:45:00", formatter));
            accountRepository.save(account3);

            Account account4 = new Account();
            account4.setUser(user3);
            account4.setIban("NL71ABNA0123456791");
            account4.setAccountType("CHECKING");
            account4.setBalance(new BigDecimal("3200.75"));
            account4.setAbsoluteTransferLimit(new BigDecimal("10000.00"));
            account4.setDailyTransferLimit(new BigDecimal("20000.00"));
            account4.setActive(true);
            account4.setCreatedAt(LocalDateTime.parse("2024-01-17 09:15:00", formatter));
            accountRepository.save(account4);

            Account account5 = new Account();
            account5.setUser(user4);
            account5.setIban("NL81ABNA0123456792");
            account5.setAccountType("SAVINGS");
            account5.setBalance(new BigDecimal("50000.00"));
            account5.setAbsoluteTransferLimit(new BigDecimal("50000.00"));
            account5.setDailyTransferLimit(new BigDecimal("100000.00"));
            account5.setActive(true);
            account5.setCreatedAt(LocalDateTime.parse("2024-01-18 11:30:00", formatter));
            accountRepository.save(account5);

            // Create Transactions
            Transaction transaction1 = new Transaction();
            transaction1.setFromAccountId(1L);
            transaction1.setToAccountId(3L);
            transaction1.setInitiatedByUserId(1L);
            transaction1.setAmount(new BigDecimal("250.00"));
            transaction1.setTransactionType("TRANSFER");
            transaction1.setCurrency("EUR");
            transaction1.setTimestamp(LocalDateTime.parse("2024-01-20 10:15:00", formatter));
            transaction1.setStatus("COMPLETED");
            transaction1.setDescription("Payment for invoice #INV001");
            transactionRepository.save(transaction1);

            Transaction transaction2 = new Transaction();
            transaction2.setFromAccountId(3L);
            transaction2.setToAccountId(1L);
            transaction2.setInitiatedByUserId(2L);
            transaction2.setAmount(new BigDecimal("150.50"));
            transaction2.setTransactionType("TRANSFER");
            transaction2.setCurrency("EUR");
            transaction2.setTimestamp(LocalDateTime.parse("2024-01-21 12:30:00", formatter));
            transaction2.setStatus("COMPLETED");
            transaction2.setDescription("Reimbursement for expenses");
            transactionRepository.save(transaction2);

            Transaction transaction3 = new Transaction();
            transaction3.setFromAccountId(1L);
            transaction3.setToAccountId(4L);
            transaction3.setInitiatedByUserId(1L);
            transaction3.setAmount(new BigDecimal("500.00"));
            transaction3.setTransactionType("TRANSFER");
            transaction3.setCurrency("EUR");
            transaction3.setTimestamp(LocalDateTime.parse("2024-01-22 14:00:00", formatter));
            transaction3.setStatus("COMPLETED");
            transaction3.setDescription("Salary payment");
            transactionRepository.save(transaction3);

            Transaction transaction4 = new Transaction();
            transaction4.setFromAccountId(2L);
            transaction4.setToAccountId(5L);
            transaction4.setInitiatedByUserId(1L);
            transaction4.setAmount(new BigDecimal("1000.00"));
            transaction4.setTransactionType("TRANSFER");
            transaction4.setCurrency("EUR");
            transaction4.setTimestamp(LocalDateTime.parse("2024-01-23 09:45:00", formatter));
            transaction4.setStatus("PENDING");
            transaction4.setDescription("Large transfer pending approval");
            transactionRepository.save(transaction4);

            Transaction transaction5 = new Transaction();
            transaction5.setFromAccountId(4L);
            transaction5.setToAccountId(1L);
            transaction5.setInitiatedByUserId(3L);
            transaction5.setAmount(new BigDecimal("75.25"));
            transaction5.setTransactionType("TRANSFER");
            transaction5.setCurrency("EUR");
            transaction5.setTimestamp(LocalDateTime.parse("2024-01-24 11:20:00", formatter));
            transaction5.setStatus("COMPLETED");
            transaction5.setDescription("Payment for services rendered");
            transactionRepository.save(transaction5);


            System.out.println("✓ Database initialized with sample data!");
            System.out.println("✓ 5 Users, 5 Accounts, 5 Transactions created");
        }
    }
}

