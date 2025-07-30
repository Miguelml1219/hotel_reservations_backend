package com.hotel.reservations.repository;

import com.hotel.reservations.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation>findByRoomIdAndDepartureDateAfterAndDateEntryBefore(
            String room_id, LocalDateTime entry, LocalDateTime exit);
}
