package org.nmg.demo.jpasecuritynmgdemo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nmg.demo.jpasecuritynmgdemo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Wyciągamy nagłówek "Authorization" z żądania
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // Sprawdzamy czy nagłówek zaczyna się od "Bearer " i wyciągamy token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7); // Usuwamy "Bearer "
            try {
                username = jwtUtil.extractUsername(jwtToken); // Wyciągamy username z tokenu
            } catch (Exception e) {
                logger.error("Nie można pobrać nazwy użytkownika z tokenu JWT", e);
            }
        }

        // Sprawdzamy czy mamy username i użytkownik jeszcze nie jest zalogowany
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Ładujemy dane użytkownika z bazy
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Sprawdzamy czy token jest OK (nie wygasł, podpis się zgadza)
            if (jwtUtil.validateToken(jwtToken, userDetails)) {

                // Tworzymy obiekt uwierzytelnienia Spring Security
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Logujemy użytkownika do Spring Security - teraz wie kto to jest
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // Puszczamy żądanie dalej
        chain.doFilter(request, response);
    }
}