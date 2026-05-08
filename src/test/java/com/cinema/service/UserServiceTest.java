package com.cinema.service;

import com.cinema.dto.RegisterRequestDTO;
import com.cinema.dto.UserDTO;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ForbiddenOperationException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Role;
import com.cinema.model.User;
import com.cinema.repository.UserRepository;
import com.cinema.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;
    private RegisterRequestDTO registerRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Mahjoub");
        user.setEmail("mahjoub@cinema.com");
        user.setPassword("password123");
        user.setRole(Role.ROLE_USER);

        userDTO = new UserDTO(1L, "mahjoub@cinema.com", "Mahjoub", "ROLE_USER");
        registerRequest = new RegisterRequestDTO("Mahjoub", "mahjoub@cinema.com", "password123");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.register(registerRequest);

        assertNotNull(result);
        assertEquals("mahjoub@cinema.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.register(registerRequest));

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfully() {
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail(), user.getRole().name())).thenReturn("jwt-token");

        String token = userService.login("mahjoub@cinema.com", "password123");

        assertNotNull(token);
        assertEquals("jwt-token", token);
    }

    @Test
    void shouldThrowExceptionWhenPasswordInvalid() {
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.login("mahjoub@cinema.com", "wrongpassword"));

        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void shouldFindUserByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("mahjoub@cinema.com");

        assertTrue(result.isPresent());
        assertEquals("Mahjoub", result.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@cinema.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("unknown@cinema.com");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldDeleteUser() {
        User currentUser = new User();
        currentUser.setId(99L);
        currentUser.setEmail("admin@cinema.com");
        currentUser.setRole(Role.ROLE_ADMIN);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                currentUser.getEmail(), null, java.util.List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateAdminSuccessfully() {
        when(userRepository.existsByEmail("admin@cinema.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        UserDTO result = userService.createAdmin("Admin", "admin@cinema.com", "adminpass");

        assertNotNull(result);
        assertEquals("admin@cinema.com", result.getEmail());
        assertEquals("ROLE_ADMIN", result.getRole());
    }

    @Test
    void shouldThrowExceptionWhenAdminEmailAlreadyExists() {
        when(userRepository.existsByEmail("admin@cinema.com")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.createAdmin("Admin", "admin@cinema.com", "adminpass"));

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    // --- Promote User Tests ---

    private void setAuthContext(String email, Role role) {
        User authUser = new User();
        authUser.setId(99L);
        authUser.setEmail(email);
        authUser.setRole(role);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                email, null, java.util.List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(authUser));
    }

    @Test
    void shouldPromoteUserToAdmin() {
        setAuthContext("main@cinema.com", Role.ROLE_MAIN_ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = userService.promoteUser(1L);

        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getRole());
    }

    @Test
    void shouldThrowWhenNonMainAdminPromotesUser() {
        setAuthContext("admin@cinema.com", Role.ROLE_ADMIN);

        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
                () -> userService.promoteUser(1L));
        assertEquals("Only MAIN_ADMIN can promote users to admin", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPromotingMainAdmin() {
        setAuthContext("main@cinema.com", Role.ROLE_MAIN_ADMIN);
        User mainAdmin = new User();
        mainAdmin.setId(2L);
        mainAdmin.setRole(Role.ROLE_MAIN_ADMIN);
        when(userRepository.findById(2L)).thenReturn(Optional.of(mainAdmin));

        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
                () -> userService.promoteUser(2L));
        assertEquals("Cannot modify MAIN_ADMIN role", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPromotingAlreadyAdmin() {
        setAuthContext("main@cinema.com", Role.ROLE_MAIN_ADMIN);
        user.setRole(Role.ROLE_ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.promoteUser(1L));
        assertEquals("User is already an ADMIN", exception.getMessage());
    }

    // --- Demote User Tests ---

    @Test
    void shouldDemoteAdminToUser() {
        setAuthContext("main@cinema.com", Role.ROLE_MAIN_ADMIN);
        user.setRole(Role.ROLE_ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = userService.demoteUser(1L);

        assertNotNull(result);
        assertEquals("ROLE_USER", result.getRole());
    }

    @Test
    void shouldThrowWhenNonMainAdminDemotesUser() {
        setAuthContext("admin@cinema.com", Role.ROLE_ADMIN);

        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
                () -> userService.demoteUser(1L));
        assertEquals("Only MAIN_ADMIN can demote admins", exception.getMessage());
    }

    @Test
    void shouldThrowWhenDemotingMainAdmin() {
        setAuthContext("main@cinema.com", Role.ROLE_MAIN_ADMIN);
        User mainAdmin = new User();
        mainAdmin.setId(2L);
        mainAdmin.setRole(Role.ROLE_MAIN_ADMIN);
        when(userRepository.findById(2L)).thenReturn(Optional.of(mainAdmin));

        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
                () -> userService.demoteUser(2L));
        assertEquals("Cannot modify MAIN_ADMIN role", exception.getMessage());
    }

    @Test
    void shouldThrowWhenDemotingAlreadyUser() {
        setAuthContext("main@cinema.com", Role.ROLE_MAIN_ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.demoteUser(1L));
        assertEquals("User is already a regular USER", exception.getMessage());
    }

    // --- Delete User Tests ---

    @Test
    void shouldDeleteUserAsAdmin() {
        setAuthContext("admin@cinema.com", Role.ROLE_ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldDeleteAdminAsMainAdmin() {
        setAuthContext("main@cinema.com", Role.ROLE_MAIN_ADMIN);
        user.setRole(Role.ROLE_ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingSelf() {
        User selfUser = new User();
        selfUser.setId(1L);
        selfUser.setEmail("mahjoub@cinema.com");
        selfUser.setRole(Role.ROLE_ADMIN);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "mahjoub@cinema.com", null, java.util.List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findByEmail("mahjoub@cinema.com")).thenReturn(Optional.of(selfUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(selfUser));

        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
                () -> userService.deleteUser(1L));
        assertEquals("You cannot delete yourself", exception.getMessage());
    }

    @Test
    void shouldThrowWhenDeletingMainAdmin() {
        setAuthContext("main@cinema.com", Role.ROLE_MAIN_ADMIN);
        User mainAdmin = new User();
        mainAdmin.setId(2L);
        mainAdmin.setRole(Role.ROLE_MAIN_ADMIN);
        when(userRepository.findById(2L)).thenReturn(Optional.of(mainAdmin));

        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
                () -> userService.deleteUser(2L));
        assertEquals("MAIN_ADMIN cannot be deleted", exception.getMessage());
    }

    @Test
    void shouldThrowWhenAdminDeletesAnotherAdmin() {
        setAuthContext("admin@cinema.com", Role.ROLE_ADMIN);
        user.setRole(Role.ROLE_ADMIN);
        User currentAdmin = new User();
        currentAdmin.setId(99L);
        currentAdmin.setEmail("admin@cinema.com");
        currentAdmin.setRole(Role.ROLE_ADMIN);
        when(userRepository.findByEmail("admin@cinema.com")).thenReturn(Optional.of(currentAdmin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
                () -> userService.deleteUser(1L));
        assertEquals("Admins can only delete regular users", exception.getMessage());
    }
}