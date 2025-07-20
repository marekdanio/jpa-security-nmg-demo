package org.nmg.demo.jpasecuritynmgdemo.controller;

import jakarta.validation.Valid;
import org.nmg.demo.jpasecuritynmgdemo.dto.AuthRequest;
import org.nmg.demo.jpasecuritynmgdemo.dto.AuthResponse;
import org.nmg.demo.jpasecuritynmgdemo.entity.User;
import org.nmg.demo.jpasecuritynmgdemo.repository.UserRepository;
import org.nmg.demo.jpasecuritynmgdemo.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            // Uwierzytelniamy użytkownika
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            // Jeśli uwierzytelnienie się powiedzie, generujemy token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            final String token = jwtUtil.generateToken(userDetails);

            // Pobieramy dodatkowe informacje o użytkowniku
            User user = userRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole().name());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Nieprawidłowe dane uwierzytelniające");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd serwera: " + e.getMessage());
        }
    }
}