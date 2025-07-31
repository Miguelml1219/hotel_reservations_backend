package com.hotel.reservations.controller;

import com.hotel.reservations.dto.ReservationRequestDTO;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.response.ApiResponse;
import com.hotel.reservations.service.ReservationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/hotel/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> obtainAll(){
        return ResponseEntity.ok(reservationService.obtainAll());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Reservation>> createReservation(@Valid @RequestBody ReservationRequestDTO dto){
        Reservation reservation = reservationService.mapAndCreateReservation(dto);
        ApiResponse<Reservation> response = new ApiResponse<>(
                reservation,
                "SUCCESS",
                "Reservation created successfully."
        );
        log.info("Reservation successfully registered. ID reservation: {}", reservation.getId());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/busy")
    public ResponseEntity<List<Room>> obtainBusyRooms(
            @RequestParam String dateEntry,
            @RequestParam String departureDate){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        LocalDateTime entry = LocalDateTime.parse(dateEntry,formatter);
        LocalDateTime exit = LocalDateTime.parse(departureDate, formatter);

        List<Room> busy = reservationService.obtainBusyRooms(entry, exit);

        return ResponseEntity.ok(busy);

    }

}
