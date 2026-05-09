package com.cinema.initializer;

import com.cinema.model.Role;
import com.cinema.model.User;
import com.cinema.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainAdminInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MainAdminInitializer mainAdminInitializer;

    @Test
    void shouldCreateMainAdminWhenNotExists() {
        ReflectionTestUtils.setField(mainAdminInitializer, "mainAdminEmail", "mainadmin@cinema.com");
        ReflectionTestUtils.setField(mainAdminInitializer, "mainAdminPassword", "admin123");
        ReflectionTestUtils.setField(mainAdminInitializer, "mainAdminName", "Main Admin");

        when(userRepository.findByEmail("mainadmin@cinema.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        mainAdminInitializer.run();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldNotCreateMainAdminWhenExists() {
        ReflectionTestUtils.setField(mainAdminInitializer, "mainAdminEmail", "mainadmin@cinema.com");

        User existing = new User();
        existing.setEmail("mainadmin@cinema.com");
        existing.setRole(Role.ROLE_MAIN_ADMIN);
        when(userRepository.findByEmail("mainadmin@cinema.com")).thenReturn(Optional.of(existing));

        mainAdminInitializer.run();

        verify(userRepository, never()).save(any(User.class));
    }
}
