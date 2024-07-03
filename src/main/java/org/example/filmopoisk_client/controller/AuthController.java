package org.example.filmopoisk_client.controller;

import org.example.filmopoisk_client.entity.DTO.LoginRequest;
import org.example.filmopoisk_client.entity.DTO.RegistrationRequest;
import org.example.filmopoisk_client.entity.User;
import org.example.filmopoisk_client.repository.UserRepository;
import org.example.filmopoisk_client.service.CustomUserDetailsService;
import org.example.filmopoisk_client.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setUsername(registrationRequest.getUsername());
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
//        System.out.println(loginRequest.getPassword());
//        System.out.println(loginRequest.getEmail());
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            System.out.println("Аутентификация успешна: " + auth.isAuthenticated());
        } catch (AuthenticationException e) {
            System.out.println("Ошибка аутентификации: " + e.getMessage());
        }
        System.out.println("user");
        final String jwt = jwtUtil.generateToken(userDetailsService.loadUserByUsername(loginRequest.getEmail()), user.getId());
        return ResponseEntity.ok(jwt);
    }

}
