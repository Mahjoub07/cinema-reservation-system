package com.cinema.initializer;

import com.cinema.model.User;
import com.cinema.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_ADMIN_EMAIL = "admin1@isi.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "ADMIN#ISI26";
    private static final String DEFAULT_ADMIN_NAME = "Admin";

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Optional<User> existingAdmin = userRepository.findByEmail(DEFAULT_ADMIN_EMAIL);

        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setEmail(DEFAULT_ADMIN_EMAIL);
            admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            admin.setName(DEFAULT_ADMIN_NAME);
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
        }
    }
}
