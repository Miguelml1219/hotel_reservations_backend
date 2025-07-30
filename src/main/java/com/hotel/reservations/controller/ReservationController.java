package com.hotel.reservations.controller;

import com.hotel.reservations.dto.ReservationRequestDTO;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody ReservationRequestDTO dto){
        Reservation reservation = reservationService.mapAndCreateReservation(dto);
        return ResponseEntity.ok(reservation);
    }
}
