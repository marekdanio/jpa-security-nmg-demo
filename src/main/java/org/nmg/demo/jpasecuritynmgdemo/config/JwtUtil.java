
package org.nmg.demo.jpasecuritynmgdemo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret; // Tajny klucz z application.properties

    @Value("${jwt.expiration}")
    private Long expiration; // Ile token ma żyć (w milisekundach)

    // Tworzy klucz do podpisywania tokenów
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Wyciąga username z tokenu
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Wyciąga datę wygaśnięcia z tokenu
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Uniwersalna metoda do wyciągania danych z tokenu
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Dekoduje token i wyciąga wszystkie dane z niego
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Sprawdza podpis
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Sprawdza czy token już nie wygasł
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Tworzy nowy token dla użytkownika
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // Buduje token JWT z danymi
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims) // Dodatkowe dane
                .subject(subject) // Username
                .issuedAt(new Date(System.currentTimeMillis())) // Kiedy utworzony
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Kiedy wygasa
                .signWith(getSigningKey()) // Podpisuje tajnym kluczem
                .compact(); // Pakuje do stringa
    }

    // Sprawdza czy token jest OK (username się zgadza + nie wygasł)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}