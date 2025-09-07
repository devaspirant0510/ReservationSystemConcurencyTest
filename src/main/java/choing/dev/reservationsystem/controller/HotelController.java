package choing.dev.reservationsystem.controller;

import choing.dev.reservationsystem.service.HotelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@Slf4j
public class HotelController {
    private final HotelService hotelService;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("rooms",hotelService.getAllHotelRooms());
        return "home";
    }
    @GetMapping("/{roomId}")
    public String hotelDetail(@PathVariable Long roomId,
                              @RequestParam(required = false) Integer year,
                              @RequestParam(required = false) Integer month,
                              Model model){
        YearMonth ym = (year!=null && month!=null) ? YearMonth.of(year, month) : YearMonth.now();
        Map<java.time.LocalDate, Long> availability = hotelService.getAvailabilityForMonth(roomId, ym);
        model.addAttribute("room", hotelService.getRoom(roomId));
        model.addAttribute("year", ym.getYear());
        model.addAttribute("month", ym.getMonthValue());
        model.addAttribute("availability", availability);
        return "detail";
    }

    @GetMapping("/{roomId}/availability")
    @ResponseBody
    public Map<String, Long> availabilityApi(@PathVariable Long roomId,
                                             @RequestParam int year,
                                             @RequestParam int month){
        YearMonth ym = YearMonth.of(year, month);
        return hotelService.getAvailabilityForMonth(roomId, ym)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));
    }

    @PostMapping("/reservations")
    public String reserveHotel(Long roomId,LocalDate checkIn, LocalDate checkOut,Long capacity,Long totalPrice){
        log.info(checkIn.toString());
        log.info(checkOut.toString());
        log.info(capacity.toString());
        log.info(totalPrice.toString());
        hotelService.reserve(roomId, checkIn,checkOut,capacity,totalPrice);

        return "home";

    }

}
