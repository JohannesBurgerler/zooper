package com.trade4life.zooper.controller;

import com.trade4life.zooper.dto.*;
import com.trade4life.zooper.model.Role;
import com.trade4life.zooper.model.User;
import com.trade4life.zooper.security.JwtUtils;
import com.trade4life.zooper.security.UserDetailsImpl;
import com.trade4life.zooper.service.TokenBlacklistService;
import com.trade4life.zooper.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenBlacklistService tokenBlacklistService ;

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
        try {
            User user = userService.findByUsernameOrEmail(loginRequest.getUsernameOrEmail());
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(user.getUsername());


            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())));
        } catch( Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse("Login failed: " + e.getMessage()));
        }

    }

    @PostMapping("/auth/logout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userLogout(@RequestHeader("Authorization") String requestHeader){
        //TODO:add blacklisting the token here

        try {
            if (StringUtils.hasText(requestHeader) && requestHeader.startsWith("Bearer ")) {
                String jwtToken = requestHeader.substring(7);
                tokenBlacklistService.blacklistToken(jwtToken);

                SecurityContextHolder.clearContext();

                return ResponseEntity.ok(new MessageResponse("Logged out successfully!"));
            }
            else {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid request! No bearer token in header!"));
            }
        } catch(RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse("Unable to blacklist token! Logout failed! error: " + e.getMessage()));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails){
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return ResponseEntity.ok(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }


    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsersByAdmin(){
        List<User> users = userService.getAllUsers();
        List<UserInfoResponse> userInfoResponseList = new java.util.ArrayList<>(List.of());

        for(User user : users){
            List<String> roles = user.getRoles().stream().map(role -> role.getRoleName().toString()).toList();
            userInfoResponseList.add(new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail(), roles));
        }

        return ResponseEntity.ok(userInfoResponseList);
    }

    @DeleteMapping("/admin/users/{userId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserByAdmin(@PathVariable Long userId){
        try{
            userService.deleteUser(userId);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully, ID: " + userId));
        } catch(RuntimeException e){
            return ResponseEntity.badRequest().body(new MessageResponse("Uer could not be deleted: " + e.getMessage()));
        }
    }

    // TODO: I should consider using username instead of ID here
    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserByAdmin(@PathVariable Long userId){
        User user = userService.findByUserId(userId);

        List<String> roles = user.getRoles().stream().map(role -> role.getRoleName().toString()).toList();

        UserInfoResponse userInfoResponse = new UserInfoResponse(user.getId(),user.getUsername(),user.getEmail(),roles);

        return ResponseEntity.ok(userInfoResponse);
    }

    @PostMapping("/admin/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long userId, @Valid @RequestBody UpdateRolesRequest updateRolesRequest){
        User user = userService.updateUserRoles(userId, updateRolesRequest.getRoles());

        List<String> roles = user.getRoles().stream().map(role -> role.getRoleName().toString()).toList();

        return ResponseEntity.ok(new UserInfoResponse(user.getId(),user.getUsername(),user.getEmail(),roles));
    }
}
