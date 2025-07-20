package org.nmg.demo.jpasecuritynmgdemo.repository;

import org.nmg.demo.jpasecuritynmgdemo.entity.Game;
import org.nmg.demo.jpasecuritynmgdemo.entity.GameRental;
import org.nmg.demo.jpasecuritynmgdemo.entity.RentalStatus;
import org.nmg.demo.jpasecuritynmgdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRentalRepository extends JpaRepository<GameRental, Long> {

    List<GameRental> findByGameIdAndUserId(Long gameId, Long userId);
    List<GameRental> findByStatus(RentalStatus status);

    List<GameRental> findByUserOrderByCreatedAtDesc(User user);

    List<GameRental> findByUserAndStatus(User user, RentalStatus status);

    List<GameRental> findByUserAndGameAndStatus(User user, Game game, RentalStatus status);

    List<GameRental> findAllByOrderByCreatedAtDesc();
}