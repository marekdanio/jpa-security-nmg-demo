package repository;

import org.nmg.demo.jpasecuritynmgdemo.entity.GameRental;
import org.nmg.demo.jpasecuritynmgdemo.entity.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GameRentalRepository extends JpaRepository<GameRental, Long> {
    List<GameRental> findByUserIdAndStatus(Long userId, RentalStatus status);
    List<GameRental> findByGameIdAndStatus(Long gameId, RentalStatus status);
    List<GameRental> findByStatus(RentalStatus status);

    @Query("SELECT gr FROM GameRental gr WHERE gr.dueDate < :currentDate AND gr.status = :status")
    List<GameRental> findOverdueRentals(@Param("currentDate") LocalDate currentDate,
                                        @Param("status") RentalStatus status);

    @Query("SELECT COUNT(gr) FROM GameRental gr WHERE gr.user.id = :userId AND gr.status = :status")
    long countActiveRentalsByUser(@Param("userId") Long userId, @Param("status") RentalStatus status);
}

