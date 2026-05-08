package com.cinema.initializer;

import com.cinema.model.Role;
import com.cinema.model.User;
import com.cinema.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MainAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.main-admin.email:mainadmin@cinema.com}")
    private String mainAdminEmail;

    @Value("${app.main-admin.password:admin123}")
    private String mainAdminPassword;

    @Value("${app.main-admin.name:Main Admin}")
    private String mainAdminName;

    public MainAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Optional<User> existingMainAdmin = userRepository.findByEmail(mainAdminEmail);

        if (existingMainAdmin.isEmpty()) {
            User mainAdmin = new User();
            mainAdmin.setEmail(mainAdminEmail);
            mainAdmin.setPassword(passwordEncoder.encode(mainAdminPassword));
            mainAdmin.setName(mainAdminName);
            mainAdmin.setRole(Role.ROLE_MAIN_ADMIN);
            userRepository.save(mainAdmin);
        }
    }
}
