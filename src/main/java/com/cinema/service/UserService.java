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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(Role.ROLE_USER);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO createAdmin(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already in use");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(Role.ROLE_ADMIN);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        return jwtUtil.generateToken(email, user.getRole().name());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Promote USER to ADMIN - only MAIN_ADMIN
    public UserDTO promoteUser(Long targetUserId) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser.getRole() != Role.ROLE_MAIN_ADMIN) {
            throw new ForbiddenOperationException("Only MAIN_ADMIN can promote users to admin");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (targetUser.getRole() == Role.ROLE_MAIN_ADMIN) {
            throw new ForbiddenOperationException("Cannot modify MAIN_ADMIN role");
        }
        if (targetUser.getRole() == Role.ROLE_ADMIN) {
            throw new BadRequestException("User is already an ADMIN");
        }

        targetUser.setRole(Role.ROLE_ADMIN);
        return convertToDTO(userRepository.save(targetUser));
    }

    // Demote ADMIN to USER - only MAIN_ADMIN
    public UserDTO demoteUser(Long targetUserId) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser.getRole() != Role.ROLE_MAIN_ADMIN) {
            throw new ForbiddenOperationException("Only MAIN_ADMIN can demote admins");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (targetUser.getRole() == Role.ROLE_MAIN_ADMIN) {
            throw new ForbiddenOperationException("Cannot modify MAIN_ADMIN role");
        }
        if (targetUser.getRole() == Role.ROLE_USER) {
            throw new BadRequestException("User is already a regular USER");
        }

        targetUser.setRole(Role.ROLE_USER);
        return convertToDTO(userRepository.save(targetUser));
    }

    // Delete user with proper role checks
    public void deleteUser(Long targetUserId) {
        User currentUser = getCurrentAuthenticatedUser();
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Self-protection
        if (currentUser.getId().equals(targetUserId)) {
            throw new ForbiddenOperationException("You cannot delete yourself");
        }

        // MAIN_ADMIN cannot be deleted
        if (targetUser.getRole() == Role.ROLE_MAIN_ADMIN) {
            throw new ForbiddenOperationException("MAIN_ADMIN cannot be deleted");
        }

        // ADMIN can only delete USERs
        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            if (targetUser.getRole() != Role.ROLE_USER) {
                throw new ForbiddenOperationException("Admins can only delete regular users");
            }
        }

        // MAIN_ADMIN can delete USER and ADMIN
        if (currentUser.getRole() != Role.ROLE_MAIN_ADMIN && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenOperationException("Insufficient permissions to delete user");
        }

        userRepository.deleteById(targetUserId);
    }

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ForbiddenOperationException("Not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getEmail(), user.getName(), user.getRole().name());
    }
}