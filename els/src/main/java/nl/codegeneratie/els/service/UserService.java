package nl.codegeneratie.els.service;

import nl.codegeneratie.els.domain.Account;
import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.domain.enums.UserRole;
import nl.codegeneratie.els.dtos.AccountDTO;
import nl.codegeneratie.els.dtos.CustomerSearchDTO;
import nl.codegeneratie.els.dtos.TokenResponseDTO;
import nl.codegeneratie.els.dtos.UserDTO;
import nl.codegeneratie.els.dtos.UserWithAccountsDTO;
import nl.codegeneratie.els.exceptions.IbanNotFoundException;
import nl.codegeneratie.els.exceptions.UserNotFoundException;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.UserRepository;
import nl.codegeneratie.els.security.JwtService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
    }

    public List<UserWithAccountsDTO> getAllUsers(Integer offset, Integer limit) {
        List<User> users = userRepository.findAll();
        int safeOffset = offset == null ? 0 : Math.max(offset, 0);
        int safeLimit = limit == null ? users.size() : Math.max(limit, 1);
        int end = Math.min(safeOffset + safeLimit, users.size());
        if (safeOffset >= users.size()) {
            return List.of();
        }
        return users.subList(safeOffset, end)
                .stream()
                .map(this::convertToUserWithAccountsDTO)
                .collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword() == null ? "" : userDTO.getPassword()));
        user.setApproved(false);
        if (user.getRole() == null) {
            user.setRole(UserRole.CUSTOMER);
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserWithAccountsDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return convertToUserWithAccountsDTO(user);
    }

    public TokenResponseDTO login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        boolean valid = passwordEncoder.matches(
                password == null ? "" : password,
                user.getPasswordHash()
        );

        if (!valid) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new TokenResponseDTO(token);
    }

    public UserWithAccountsDTO approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setApproved(true);
        userRepository.save(user);

        List<Account> existing = accountRepository.findByUser_Id(userId);
        if (existing.isEmpty()) {
            accountRepository.save(buildDefaultAccount(user, "checking"));
            accountRepository.save(buildDefaultAccount(user, "savings"));
        }

        return convertToUserWithAccountsDTO(user);
    }

    public List<CustomerSearchDTO> searchCustomers(String firstName, String lastName) {
        List<User> users = userRepository.findByFirstAndLastName(firstName, lastName);
        List<CustomerSearchDTO> results = new ArrayList<>();
        for (User user : users) {
            List<Account> accounts = accountRepository.findByUser_Id(user.getId());
            for (Account account : accounts) {
                results.add(new CustomerSearchDTO(user.getId(), user.getFirstName(), user.getLastName(), account.getIban()));
            }
        }
        return results;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setPassword(null);
        return userDTO;
    }

    private UserWithAccountsDTO convertToUserWithAccountsDTO(User user) {
        UserWithAccountsDTO dto = new UserWithAccountsDTO();
        BeanUtils.copyProperties(user, dto);
        dto.setPassword(null);
        List<AccountDTO> accounts = accountRepository.findByUser_Id(user.getId())
                .stream()
                .map(this::toAccountDTO)
                .collect(Collectors.toList());
        dto.setAccounts(accounts);
        return dto;
    }

    private AccountDTO toAccountDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        BeanUtils.copyProperties(account, dto);
        return dto;
    }

    private Account buildDefaultAccount(User user, String accountType) {
        Account account = new Account();
        account.setUser(user);
        account.setIban("NL" + (90 + (int) (Math.random() * 10)) + "ELS" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setAbsoluteTransferLimit(new BigDecimal("1000.00"));
        account.setDailyTransferLimit(new BigDecimal("5000.00"));
        account.setActive(true);
        account.setCreatedAt(LocalDateTime.now());
        return account;
    }
}

