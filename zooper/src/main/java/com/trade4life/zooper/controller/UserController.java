package com.trade4life.zooper.controller;

import com.trade4life.zooper.dto.MessageResponse;
import com.trade4life.zooper.dto.SignupRequest;
import com.trade4life.zooper.model.User;
import com.trade4life.zooper.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> userSignUp(@Valid @RequestBody SignupRequest signupRequest) {
        try{
            User user = userService.registerUser(signupRequest);
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch(RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public String userLogIn(){
        return "userloggedin";
    }

}
