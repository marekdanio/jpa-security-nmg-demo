package org.nmg.demo.jpasecuritynmgdemo.controller;

import org.nmg.demo.jpasecuritynmgdemo.entity.Game;
import org.nmg.demo.jpasecuritynmgdemo.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
@CrossOrigin
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    // Pobierz wszystkie gry z paginacją
    @GetMapping
    public ResponseEntity<Page<Game>> getAllGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Game> games = gameRepository.findAll(pageable);
        return ResponseEntity.ok(games);
    }

    // Pobierz grę po ID
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        Optional<Game> game = gameRepository.findById(id);
        return game.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Wyszukaj gry po tytule
    @GetMapping("/search")
    public ResponseEntity<List<Game>> searchGames(@RequestParam String title) {
        List<Game> games = gameRepository.findByTitleContainingIgnoreCase(title);
        return ResponseEntity.ok(games);
    }

    // Filtruj gry po gatunku
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Game>> getGamesByGenre(@PathVariable String genre) {
        List<Game> games = gameRepository.findByGenreIgnoreCase(genre);
        return ResponseEntity.ok(games);
    }

    // Filtruj gry po platformie
    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<Game>> getGamesByPlatform(@PathVariable String platform) {
        List<Game> games = gameRepository.findByPlatformIgnoreCase(platform);
        return ResponseEntity.ok(games);
    }

    // R - READ
    // Pobierz dostępne gry
    @GetMapping("/available")
    public ResponseEntity<List<Game>> getAvailableGames() {
        List<Game> games = gameRepository.findAvailableGames();
        return ResponseEntity.ok(games);
    }

    // C - CREATE
    // ADMIN - Dodaj nową grę
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Game> createGame(@Valid @RequestBody Game game) {
        Game savedGame = gameRepository.save(game);
        return ResponseEntity.ok(savedGame);
    }

    // U - UPDATE
    // ADMIN - Aktualizuj grę
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @Valid @RequestBody Game gameDetails) {
        Optional<Game> optionalGame = gameRepository.findById(id);

        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            game.setTitle(gameDetails.getTitle());
            game.setDescription(gameDetails.getDescription());
            game.setGenre(gameDetails.getGenre());
            game.setPlatform(gameDetails.getPlatform());
            game.setReleaseDate(gameDetails.getReleaseDate());
            game.setTotalCopies(gameDetails.getTotalCopies());
            game.setAvailableCopies(gameDetails.getAvailableCopies());

            Game updatedGame = gameRepository.save(game);
            return ResponseEntity.ok(updatedGame);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // D - DELETE
    // ADMIN - Usuń grę
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteGame(@PathVariable Long id) {
        if (gameRepository.existsById(id)) {
            gameRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}