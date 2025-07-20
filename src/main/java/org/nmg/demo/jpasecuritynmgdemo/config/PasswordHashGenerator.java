package org.nmg.demo.jpasecuritynmgdemo.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "e5";
        String hash = encoder.encode(password);

        System.out.println("=== HASH DLA HASŁA 'e5' ===");
        System.out.println("Hasło: " + password);
        System.out.println("Hash: " + hash);

        // Sprawdź czy hash jest poprawny
        boolean matches = encoder.matches(password, hash);
        System.out.println("Czy hash jest poprawny: " + matches);
    }
}
