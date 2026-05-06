package com.cinema.controller;

import com.cinema.dto.LoginRequestDTO;
import com.cinema.dto.RegisterRequestDTO;
import com.cinema.dto.UserDTO;
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

    private UserDTO userDTO;
    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(1L, "mahjoub@cinema.com", "Mahjoub", "ROLE_USER");
        registerRequest = new RegisterRequestDTO("Mahjoub", "mahjoub@cinema.com", "password123");
        loginRequest = new LoginRequestDTO("mahjoub@cinema.com", "password123");
    }

    @Test
    void shouldRegisterUser() {
        when(userService.register(any(RegisterRequestDTO.class))).thenReturn(userDTO);

        UserDTO result = userController.register(registerRequest).getBody();

        assertNotNull(result);
        assertEquals("mahjoub@cinema.com", result.getEmail());
        verify(userService, times(1)).register(registerRequest);
    }

    @Test
    void shouldLoginUser() {
        when(userService.login(any(), any())).thenReturn("jwt-token");

        Map<String, String> result = userController.login(loginRequest).getBody();

        assertNotNull(result);
        assertEquals("jwt-token", result.get("token"));
    }

    @Test
    void shouldGetAllUsers() {
        when(userService.findAll()).thenReturn(Arrays.asList(userDTO));

        List<UserDTO> result = userController.getAllUsers().getBody();

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