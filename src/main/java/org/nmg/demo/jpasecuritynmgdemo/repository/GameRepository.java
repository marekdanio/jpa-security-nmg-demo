
package org.nmg.demo.jpasecuritynmgdemo.repository;

import org.nmg.demo.jpasecuritynmgdemo.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByTitleContainingIgnoreCase(String title);
    List<Game> findByGenreIgnoreCase(String genre);
    List<Game> findByPlatformIgnoreCase(String platform);

    @Query("SELECT g FROM Game g WHERE g.availableCopies > 0")
    List<Game> findAvailableGames();
}