package com.trade4life.zooper.service;

import com.trade4life.zooper.dto.SignupRequest;
import com.trade4life.zooper.model.ERole;
import com.trade4life.zooper.model.Role;
import com.trade4life.zooper.model.User;
import com.trade4life.zooper.repository.RoleRepository;
import com.trade4life.zooper.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;

    }

    public User registerUser(SignupRequest signupRequest){
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            throw new RuntimeException("A user with this email already exists!");
        }

        if(userRepository.existsByUsername(signupRequest.getUsername())){
            throw new RuntimeException("A user with this username already exists!");
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
        roles.add(userRole);
        user.setRoles(roles);


        try {
            //userRepository.manualRegister(user.getEmail(), user.getPassword(), user.getUsername());
            userRepository.save(user);
            return user;
        } catch (DataAccessException e){
            throw new RuntimeException("Database error while registering the new user: " + e.getMessage());
        }
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found with email: " + email));
    }
    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("user not found with username: " + username));
    }
    public User findByUsernameOrEmail(String usernameOrEmail){
        return userRepository.findByUsername(usernameOrEmail).orElseGet(() -> userRepository.findByEmail(usernameOrEmail).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public User findByUserId(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Error: User can not be found with Id: " + userId));
        return user;
    }

    // Add messageresponse here for successful deletion and token blacklisting
    public void deleteUser(Long userId){
        if(!userRepository.existsById(userId)){
            throw new RuntimeException("User not found with id: " + userId);
        }

        userRepository.deleteById(userId);
    }

    public User updateUserRoles(Long userId, Set<String> roleNames){
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Error: User can not be found with Id: " + userId));

        Set<Role> roles = new HashSet<>();
        for(String roleName : roleNames){
            ERole eRole = ERole.valueOf(roleName);
            roles.add(new Role(eRole));
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }
}
