package com.example.eCommerce.controller;

import com.example.eCommerce.jwt.*;
import com.example.eCommerce.jwt.service.UserDetailsImpl;
import com.example.eCommerce.model.AppRole;
import com.example.eCommerce.model.Role;
import com.example.eCommerce.model.User;
import com.example.eCommerce.repositories.RoleRepository;
import com.example.eCommerce.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    public AuthenticationManager authenticationManager;

    @Autowired
    private jwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword())
            );

        } catch (AuthenticationException ex) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad Credentials");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().
                stream().map(item -> item.getAuthority()).
                toList();
        LoginResponse loginResponse = new LoginResponse(userDetails.getId(),jwtCookie.toString() ,userDetails.getUsername(), roles);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
       if(userRepository.existsByUsername(signupRequest.getUsername())) {
           return ResponseEntity.
                   badRequest()
                   .body(new MessageResponse("Error: Username is already taken"));
       }
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.
                    badRequest().
                    body(new MessageResponse("Error: Email is already in use"));
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
               passwordEncoder.encode(signupRequest.getPassword())

        );

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByAppRole(AppRole.ROLE_USER).
                    orElseThrow(()-> new RuntimeException("Error: role is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" :
                        Role adminRole = roleRepository.findByAppRole(AppRole.ROLE_ADMIN).
                                orElseThrow(()-> new RuntimeException("Error: role is not found"));;
                        roles.add(adminRole);
                        break;

                     case "seller" :
                         Role sellerRole = roleRepository.findByAppRole(AppRole.ROLE_SELLER).
                                 orElseThrow(()-> new RuntimeException("Error: role is not found"));;
                                 roles.add(sellerRole);
                                 break;
                      default :
                          Role userRole = roleRepository.findByAppRole(AppRole.ROLE_USER).
                                  orElseThrow(()-> new RuntimeException("Error: role is not found"));
                          roles.add(userRole);
                }
            });

        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication) {
        if(authentication != null) {
            return authentication.getName();
        } else {
            return "NULL";
        }
    }


    @GetMapping("/user")
    public  ResponseEntity<LoginResponse> getUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().
                stream().map(item -> item.getAuthority()).
                toList();
        LoginResponse response = new LoginResponse(userDetails.getId(),userDetails.getUsername(), roles);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookies();
        return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new MessageResponse("Successfully logged out"));
    }


}
