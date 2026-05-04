package com.cinema.controller;

import com.cinema.model.User;
import com.cinema.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

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
    void shouldRegisterUser() {
        when(userService.register(any(User.class))).thenReturn(user);

        User result = userController.register(user).getBody();

        assertNotNull(result);
        assertEquals("mahjoub@cinema.com", result.getEmail());
        verify(userService, times(1)).register(user);
    }

    @Test
    void shouldLoginUser() {
        when(userService.login(any(), any())).thenReturn("jwt-token");

        Map<String, String> loginRequest = Map.of(
                "email", "mahjoub@cinema.com",
                "password", "password123"
        );

        Map<String, String> result = userController.login(loginRequest).getBody();

        assertNotNull(result);
        assertEquals("jwt-token", result.get("token"));
    }

    @Test
    void shouldGetAllUsers() {
        when(userService.findAll()).thenReturn(Arrays.asList(user));

        List<User> result = userController.getAllUsers().getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("mahjoub@cinema.com", result.get(0).getEmail());
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(userService).deleteUser(1L);

        userController.deleteUser(1L);

        verify(userService, times(1)).deleteUser(1L);
    }
}