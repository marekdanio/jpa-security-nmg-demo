package org.nmg.demo.jpasecuritynmgdemo.controller;

import org.nmg.demo.jpasecuritynmgdemo.entity.Game;
import org.nmg.demo.jpasecuritynmgdemo.entity.GameRental;
import org.nmg.demo.jpasecuritynmgdemo.entity.RentalStatus;
import org.nmg.demo.jpasecuritynmgdemo.entity.User;
import org.nmg.demo.jpasecuritynmgdemo.repository.GameRepository;
import org.nmg.demo.jpasecuritynmgdemo.repository.GameRentalRepository;
import org.nmg.demo.jpasecuritynmgdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rentals")
@CrossOrigin
public class RentalController {

    @Autowired
    private GameRentalRepository gameRentalRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    // Pobierz wypożyczenia bieżącego użytkownika
    @GetMapping("/my")
    public ResponseEntity<List<GameRental>> getMyRentals(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        List<GameRental> rentals = gameRentalRepository.findByUserOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(rentals);
    }

    // Pobierz aktywne wypożyczenia bieżącego użytkownika
    @GetMapping("/my/active")
    public ResponseEntity<List<GameRental>> getMyActiveRentals(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        List<GameRental> rentals = gameRentalRepository.findByUserAndStatus(user, RentalStatus.ACTIVE);
        return ResponseEntity.ok(rentals);
    }

    // ADMIN - Pobierz wszystkie wypożyczenia
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GameRental>> getAllRentals() {
        List<GameRental> rentals = gameRentalRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(rentals);
    }

    // ADMIN - Pobierz wypożyczenia po statusie
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GameRental>> getRentalsByStatus(@PathVariable RentalStatus status) {
        List<GameRental> rentals = gameRentalRepository.findByStatus(status);
        return ResponseEntity.ok(rentals);
    }

    // Wypożycz grę
    @PostMapping("/rent/{gameId}")
    public ResponseEntity<?> rentGame(@PathVariable Long gameId, Authentication authentication) {
        try {
            // Znajdź użytkownika
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            // Znajdź grę
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new RuntimeException("Gra nie znaleziona"));

            // Sprawdź dostępność
            if (game.getAvailableCopies() <= 0) {
                return ResponseEntity.badRequest().body("Gra nie jest dostępna do wypożyczenia");
            }

            // Sprawdź czy użytkownik już nie wypożyczył tej gry
            List<GameRental> existingRentals = gameRentalRepository.findByUserAndGameAndStatus(user, game, RentalStatus.ACTIVE);
            if (!existingRentals.isEmpty()) {
                return ResponseEntity.badRequest().body("Już wypożyczyłeś tę grę");
            }

            // Utwórz wypożyczenie
            GameRental rental = new GameRental();
            rental.setUser(user);
            rental.setGame(game);
            rental.setRentalDate(LocalDate.now());
            rental.setDueDate(LocalDate.now().plusDays(14)); // 2 tygodnie na wypożyczenie
            rental.setStatus(RentalStatus.ACTIVE);

            GameRental savedRental = gameRentalRepository.save(rental);

            // Zmniejsz liczbę dostępnych kopii
            game.setAvailableCopies(game.getAvailableCopies() - 1);
            gameRepository.save(game);

            return ResponseEntity.ok(savedRental);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Błąd podczas wypożyczania: " + e.getMessage());
        }
    }

    // Zwróć grę po id wypożyczenia
    @PostMapping("/return/rental/{rentalId}")
    public ResponseEntity<?> returnGameByRentalId(@PathVariable Long rentalId, Authentication authentication) {
        try {
            // Znajdź wypożyczenie
            GameRental rental = gameRentalRepository.findById(rentalId)
                    .orElseThrow(() -> new RuntimeException("Wypożyczenie nie znalezione"));

            // Sprawdź czy to wypożyczenie należy do bieżącego użytkownika (chyba że admin)
            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin && !rental.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("Nie masz uprawnień do zwrotu tego wypożyczenia");
            }

            // Sprawdź czy gra nie została już zwrócona
            if (rental.getStatus() == RentalStatus.RETURNED) {
                return ResponseEntity.badRequest().body("Gra została już zwrócona");
            }

            // Zwróć grę
            rental.setReturnDate(LocalDate.now());
            rental.setStatus(RentalStatus.RETURNED);
            GameRental savedRental = gameRentalRepository.save(rental);

            // Zwiększ liczbę dostępnych kopii
            Game game = rental.getGame();
            game.setAvailableCopies(game.getAvailableCopies() + 1);
            gameRepository.save(game);

            return ResponseEntity.ok(savedRental);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Błąd podczas zwrotu: " + e.getMessage());
        }
    }

    // Zwróć grę po id gry
    @PostMapping("/return/game/{gameId}")
    public ResponseEntity<?> returnGameByGameId(@PathVariable Long gameId, Authentication authentication) {
        try {
            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            Game game = gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Gra nie znaleziona"));

            List<GameRental> existingRentals = gameRentalRepository.findByUserAndGameAndStatus(currentUser, game, RentalStatus.ACTIVE);
            if (existingRentals.isEmpty()) {
                return ResponseEntity.badRequest().body("Nie ma aktywnego wypożyczenia tej gry dla tego użytkownika");
            }

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            List<GameRental> returnedRentals = new ArrayList<>();
            for (GameRental rental : existingRentals) {
                if (!isAdmin && !rental.getUser().getId().equals(currentUser.getId())) {
                    return ResponseEntity.status(403).body("Nie masz uprawnień do zwrotu tego wypożyczenia");
                }

                // Sprawdź czy gra nie została już zwrócona
                if (rental.getStatus() == RentalStatus.RETURNED) {
                    return ResponseEntity.badRequest().body("Gra została już zwrócona");
                }

                // Zwróć grę
                rental.setReturnDate(LocalDate.now());
                rental.setStatus(RentalStatus.RETURNED);
                GameRental savedRental = gameRentalRepository.save(rental);

                // Zwiększ liczbę dostępnych kopii
                game.setAvailableCopies(game.getAvailableCopies() + 1);
                returnedRentals.add(savedRental);
                gameRepository.save(game);
            }

            return ResponseEntity.ok(returnedRentals);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Błąd podczas zwrotu: " + e.getMessage());
        }
    }
}