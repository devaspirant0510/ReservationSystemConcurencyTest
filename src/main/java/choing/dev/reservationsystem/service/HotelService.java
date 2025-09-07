package choing.dev.reservationsystem.service;

import choing.dev.reservationsystem.entity.DailyRoomInventory;
import choing.dev.reservationsystem.entity.Reservation;
import choing.dev.reservationsystem.entity.Room;
import choing.dev.reservationsystem.repository.DailyRoomInventoryRepository;
import choing.dev.reservationsystem.repository.ReservationRepository;
import choing.dev.reservationsystem.repository.RoomRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class HotelService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final DailyRoomInventoryRepository dailyRoomInventoryRepository;

    public List<Room> getAllHotelRooms() {
        return roomRepository.findAll();
    }

    @Transactional
    public void reserve(Long roomId,LocalDate startDate, LocalDate endDate,Long capacity,Long price){
        Room room = roomRepository.findById(roomId).orElseThrow();
        Reservation reservation = Reservation.builder()
                .checkIn(startDate)
                .checkOut(endDate)
                .room(room)
                .userId(1L)
                .totalPrice(price)
                .build();
        reservationRepository.save(reservation);
        // start date 부터 end date 까지 일자별로 예약 내역 확인
        for(LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)){
            log.info("Reserved date: " + date.toString());
            // 해당 일자에 대한 예약 정보가 없을경우 추가
            if(!dailyRoomInventoryRepository.existsByRoomIdAndDateTime(roomId,date)){
                dailyRoomInventoryRepository.save(
                        DailyRoomInventory
                                .builder()
                                .availableRooms(room.getRoomCapacity() - 1)
                                .room(room)
                                .dateTime(date)
                                .build()
                );
            }
            // 있을경우 카운트 감소
            else{
                DailyRoomInventory dailyRoomInventory = dailyRoomInventoryRepository.findByRoomIdAndDateTime(roomId, date).orElseThrow();
                dailyRoomInventory.setAvailableRooms(dailyRoomInventory.getAvailableRooms() - 1);
                dailyRoomInventoryRepository.save(dailyRoomInventory);

            }
        }


    }

    public Room getRoom(Long roomId){
        return roomRepository.findById(roomId).orElseThrow();
    }

    public Map<LocalDate, Long> getAvailabilityForMonth(Long roomId, YearMonth ym){
        Room room = getRoom(roomId);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<DailyRoomInventory> list = dailyRoomInventoryRepository.findByRoomIdAndDateTimeBetween(roomId,start,end);
        Map<LocalDate, Long> map = new HashMap<>();
        for(DailyRoomInventory inv : list){
            map.put(inv.getDateTime(), inv.getAvailableRooms());
        }
        for(LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)){
            map.putIfAbsent(d, room.getRoomCapacity());
        }
        return map;
    }

}
