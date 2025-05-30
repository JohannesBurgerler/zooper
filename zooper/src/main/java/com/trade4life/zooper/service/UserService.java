package com.trade4life.zooper.service;

import com.trade4life.zooper.dto.SignupRequest;
import com.trade4life.zooper.model.User;
import com.trade4life.zooper.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        try {
            userRepository.manualRegister(user.getEmail(), user.getPassword(), user.getUsername());
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
}
