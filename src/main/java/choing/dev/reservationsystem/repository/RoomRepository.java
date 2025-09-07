package choing.dev.reservationsystem.repository;

import choing.dev.reservationsystem.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
