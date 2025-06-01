package com.trade4life.zooper.controller;

import com.trade4life.zooper.dto.JwtResponse;
import com.trade4life.zooper.dto.MessageResponse;
import com.trade4life.zooper.dto.SignupRequest;
import com.trade4life.zooper.model.User;
import com.trade4life.zooper.security.JwtUtils;
import com.trade4life.zooper.security.UserDetailsImpl;
import com.trade4life.zooper.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.trade4life.zooper.dto.LoginRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/auth/signup")
    public ResponseEntity<?> userSignUp(@Valid @RequestBody SignupRequest signupRequest) {
        try{
            User user = userService.registerUser(signupRequest);
            System.out.println(user.getEmail() + " : "+ user.getUsername() + " : " + signupRequest.getPassword());

            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch(RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> userLogIn(@Valid @RequestBody LoginRequest loginRequest){
        User user = userService.findByUsernameOrEmail(loginRequest.getUsernameOrEmail());
        Authentication authentication = authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(user.getUsername());


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail()));

    }

}
