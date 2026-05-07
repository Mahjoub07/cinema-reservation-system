package com.cinema.service;

import com.cinema.dto.RegisterRequestDTO;
import com.cinema.dto.UserDTO;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.User;
import com.cinema.repository.UserRepository;
import com.cinema.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        user.setRole("ROLE_USER");

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
        when(jwtUtil.generateToken(user.getEmail(), user.getRole())).thenReturn("jwt-token");

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
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
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
}