package com.cinema.service;

import com.cinema.model.User;
import com.cinema.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Mahjoub");
        user.setEmail("mahjoub@cinema.com");
        user.setPassword("password123");
        user.setRole("ROLE_USER");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(user);

        assertNotNull(result);
        assertEquals("mahjoub@cinema.com", result.getEmail());
        assertEquals("ROLE_USER", result.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.register(user));

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
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
}