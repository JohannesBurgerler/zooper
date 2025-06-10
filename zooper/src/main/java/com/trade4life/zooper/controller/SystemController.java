package com.trade4life.zooper.controller;

import com.trade4life.zooper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class SystemController {

    @Autowired
    UserRepository userRepository;
    // TODO: add more stats here
    @GetMapping("/stats")
    public ResponseEntity<?> getSystemStats(){
        long totalUsers = userRepository.count();
        return ResponseEntity.ok(new Object(){
            public final long users = totalUsers;
            public final String status = "System is running";
        });
    }
}
