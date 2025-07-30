package com.hotel.reservations.repository;

import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation>findByRoomIdAndDepartureDateAfterAndDateEntryBefore(
            String room_id, LocalDateTime entry, LocalDateTime exit);

    @Query("SELECT DISTINCT r.room FROM Reservation r " +
            "WHERE r.departureDate > :entry AND r.dateEntry < :exit")
    List<Room>findRoomsOccupiedInRange(@Param("entry") LocalDateTime entry,
                                       @Param("exit") LocalDateTime exit);
}
