package com.trade4life.zooper.config;

import com.trade4life.zooper.model.ERole;
import com.trade4life.zooper.model.Role;
import com.trade4life.zooper.model.User;
import com.trade4life.zooper.repository.RoleRepository;
import com.trade4life.zooper.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.email:admin@zooper.com}")
    private String adminEmail;

    @Value("${app.admin.password:#{null}}")
    private String adminPassword;

    @Value("${app.admin.create-on-startup:false}")
    private boolean createOnStartup;

    @Override
    public void run(String... args) throws Exception {
        // Only create admin if explicitly enabled or password is provided
        if (!createOnStartup && adminPassword == null) {
            logger.info("Admin creation skipped. Set app.admin.create-on-startup=true or provide app.admin.password");
            return;
        }

        // Check if admin already exists
        if (userRepository.existsByUsername(adminUsername)) {
            logger.info("Admin user '{}' already exists", adminUsername);
            return;
        }

        // Use provided password or generate a secure one
        String password = adminPassword;
        if (password == null) {
            password = generateSecurePassword();
            logger.warn("========================================");
            logger.warn("ADMIN USER CREATED WITH GENERATED PASSWORD");
            logger.warn("Username: {}", adminUsername);
            logger.warn("Password: {}", password);
            logger.warn("PLEASE CHANGE THIS PASSWORD IMMEDIATELY!");
            logger.warn("========================================");
        }

        // Create admin user
        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(password));

        // Assign all roles
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role USER not found")));
        roles.add(roleRepository.findByName(ERole.ROLE_TRADER)
                .orElseThrow(() -> new RuntimeException("Role MODERATOR not found")));
        roles.add(roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role ADMIN not found")));
        admin.setRoles(roles);

        userRepository.save(admin);
        logger.info("Admin user '{}' created successfully", adminUsername);
    }

    private String generateSecurePassword() {
        // Generate a secure random password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            password.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return password.toString();
    }
}
