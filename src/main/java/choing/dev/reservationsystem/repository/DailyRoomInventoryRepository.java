package choing.dev.reservationsystem.repository;

import choing.dev.reservationsystem.entity.DailyRoomInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyRoomInventoryRepository extends JpaRepository<DailyRoomInventory, Long> {
    boolean existsByRoomIdAndDateTime(Long roomId, LocalDate dateTime);
    Optional<DailyRoomInventory> findByRoomIdAndDateTime(Long roomId, LocalDate dateTime);
    List<DailyRoomInventory> findByRoomIdAndDateTimeBetween(Long roomId, LocalDate start, LocalDate end);
}
