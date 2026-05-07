package com.cinema.initializer;

import com.cinema.model.User;
import com.cinema.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminInitializer adminInitializer;

    @Test
    void shouldCreateAdminWhenNotExists() {
        when(userRepository.findByEmail("admin1@isi.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        adminInitializer.run();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldNotCreateAdminWhenExists() {
        User existing = new User();
        existing.setEmail("admin1@isi.com");
        when(userRepository.findByEmail("admin1@isi.com")).thenReturn(Optional.of(existing));

        adminInitializer.run();

        verify(userRepository, never()).save(any(User.class));
    }
}
