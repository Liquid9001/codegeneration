package nl.codegeneratie.els.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import nl.codegeneratie.els.domain.User;
import nl.codegeneratie.els.domain.enums.UserRole;
import nl.codegeneratie.els.dtos.TokenResponseDTO;
import nl.codegeneratie.els.dtos.UserDTO;
import nl.codegeneratie.els.dtos.UserWithAccountsDTO;
import nl.codegeneratie.els.exceptions.ForbiddenException;
import nl.codegeneratie.els.exceptions.InvalidCredentialsException;
import nl.codegeneratie.els.exceptions.UserNotFoundException;
import nl.codegeneratie.els.exceptions.UserRegistrationException;
import nl.codegeneratie.els.mappers.AccountMapper;
import nl.codegeneratie.els.repository.AccountRepository;
import nl.codegeneratie.els.repository.UserRepository;
import nl.codegeneratie.els.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserRepository userRepository;
    private AccountRepository accountRepository;
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        accountRepository = mock(AccountRepository.class);
        AccountService accountService = mock(AccountService.class);
        AccountMapper accountMapper = mock(AccountMapper.class);
        passwordEncoder = new BCryptPasswordEncoder();
        JwtService jwtService = new JwtService(
                "testOnlyJwtSecretKeyForElsMustBeAtLeast32Bytes",
                3600000
        );
        userService = new UserService(
                userRepository,
                accountRepository,
                accountService,
                jwtService,
                accountMapper,
                passwordEncoder
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createUserHashesPasswordAndAlwaysAssignsCustomerRole() {
        UserDTO request = validRegistrationRequest();
        request.setRole(UserRole.ADMIN);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByBsn(request.getBsn())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(42L);
            return user;
        });

        UserDTO response = userService.createUser(request);

        assertEquals(UserRole.CUSTOMER, response.getRole());
        assertFalse(response.isApproved());
        assertEquals(null, response.getPassword());
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
                user.getRole() == UserRole.CUSTOMER
                        && passwordEncoder.matches("password123", user.getPasswordHash())
        ));
    }

    @Test
    void createUserCannotCreateEmployeeRoleThroughPublicRegistration() {
        UserDTO request = validRegistrationRequest();
        request.setRole(UserRole.EMPLOYEE);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByBsn(request.getBsn())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO response = userService.createUser(request);

        assertEquals(UserRole.CUSTOMER, response.getRole());
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
                user.getRole() == UserRole.CUSTOMER
        ));
    }

    @Test
    void createUserRejectsDuplicateEmailBeforeSaving() {
        UserDTO request = validRegistrationRequest();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserRegistrationException.class, () -> userService.createUser(request));
    }

    @Test
    void createUserRejectsDuplicateBsnBeforeSaving() {
        UserDTO request = validRegistrationRequest();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByBsn(request.getBsn())).thenReturn(true);

        assertThrows(UserRegistrationException.class, () -> userService.createUser(request));
    }

    @Test
    void createUserRejectsMissingRequiredRegistrationFields() {
        UserDTO request = validRegistrationRequest();
        request.setEmail("");

        assertThrows(UserRegistrationException.class, () -> userService.createUser(request));
    }

    @Test
    void createUserRejectsOtherRequiredRegistrationFieldVariants() {
        UserDTO blankPassword = validRegistrationRequest();
        blankPassword.setPassword("");
        assertThrows(UserRegistrationException.class, () -> userService.createUser(blankPassword));

        UserDTO blankFirstName = validRegistrationRequest();
        blankFirstName.setFirstName("");
        assertThrows(UserRegistrationException.class, () -> userService.createUser(blankFirstName));

        UserDTO blankLastName = validRegistrationRequest();
        blankLastName.setLastName("");
        assertThrows(UserRegistrationException.class, () -> userService.createUser(blankLastName));

        UserDTO missingPhoneNumber = validRegistrationRequest();
        missingPhoneNumber.setPhoneNumber(null);
        assertThrows(UserRegistrationException.class, () -> userService.createUser(missingPhoneNumber));

        UserDTO missingBsn = validRegistrationRequest();
        missingBsn.setBsn(null);
        assertThrows(UserRegistrationException.class, () -> userService.createUser(missingBsn));
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        User user = existingUser();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        TokenResponseDTO response = userService.login(user.getEmail(), "password123");

        assertTrue(response.getToken().contains("."));
    }

    @Test
    void loginRejectsInvalidCredentials() {
        User user = existingUser();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> userService.login(user.getEmail(), "wrong"));
    }

    @Test
    void loginRejectsUnknownEmail() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.login("unknown@example.com", "password123"));
    }

    @Test
    void getUserByIdAllowsCustomerToAccessOwnUser() {
        User user = existingUser();
        setAuthenticatedUser(user.getId(), "CUSTOMER");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.findByUser_IdAndActiveTrue(user.getId())).thenReturn(List.of());

        UserWithAccountsDTO response = userService.getUserById(user.getId());

        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void getUserByIdRejectsCustomerAccessingAnotherUser() {
        setAuthenticatedUser(1L, "CUSTOMER");

        assertThrows(ForbiddenException.class, () -> userService.getUserById(2L));
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void getUserByIdAllowsEmployeeToAccessUserData() {
        User user = existingUser();
        setAuthenticatedUser(99L, "EMPLOYEE");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.findByUser_IdAndActiveTrue(user.getId())).thenReturn(List.of());

        UserWithAccountsDTO response = userService.getUserById(user.getId());

        assertEquals(user.getId(), response.getId());
    }

    @Test
    void getUserByIdAllowsAdminToAccessUserData() {
        User user = existingUser();
        setAuthenticatedUser(99L, "ADMIN");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountRepository.findByUser_IdAndActiveTrue(user.getId())).thenReturn(List.of());

        UserWithAccountsDTO response = userService.getUserById(user.getId());

        assertEquals(user.getId(), response.getId());
    }

    @Test
    void getUserByIdThrowsUserNotFoundForAuthorizedMissingUser() {
        setAuthenticatedUser(99L, "ADMIN");
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(404L));
    }

    private UserDTO validRegistrationRequest() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new.customer@example.com");
        userDTO.setPassword("password123");
        userDTO.setFirstName("New");
        userDTO.setLastName("Customer");
        userDTO.setPhoneNumber(612345678);
        userDTO.setBsn(123456789);
        return userDTO;
    }

    private User existingUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("existing.customer@example.com");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setRole(UserRole.CUSTOMER);
        user.setApproved(true);
        return user;
    }

    private void setAuthenticatedUser(Long userId, String role) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("role", role);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                claims,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
