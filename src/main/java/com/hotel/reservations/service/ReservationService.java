package com.hotel.reservations.service;

import com.hotel.reservations.dto.ReservationRequestDTO;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.repository.ReservationRepository;
import com.hotel.reservations.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {

        Room room = roomRepository.findById(reservation.getRoom().getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        log.error("Room with ID {} not found", reservation.getRoom().getId());

        log.info("Starting reservation creation for {} ({})", reservation.getName(), reservation.getIdentification());
        List<Reservation> overlappingBookings = reservationRepository
                .findByRoomIdAndDepartureDateAfterAndDateEntryBefore(
                        room.getId(),
                        reservation.getDateEntry(),
                        reservation.getDepartureDate()
                );

        if(!overlappingBookings.isEmpty()){

            log.warn("Room {} not available between {} and {}", reservation.getRoom().getId(),
                    reservation.getDateEntry(), reservation.getDepartureDate());

            throw new IllegalArgumentException("The room is not available in that date range.");
        }

        reservation.setRoom(room);

        log.info("Reservation successfully created for room {} from {} to {}",
                reservation.getRoom().getId(), reservation.getDateEntry(), reservation.getDepartureDate());

        return reservationRepository.save(reservation);
    }

    public List<Reservation> obtainAll() {
        log.info("Getting all registered reservations.");
        return reservationRepository.findAll();
    }

    public Reservation mapAndCreateReservation(ReservationRequestDTO dto) {
        Room room = new Room();
        room.setId(dto.getRoomId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        Reservation reservation = Reservation.builder()
                .name(dto.getName())
                .identification(dto.getIdentification())
                .documentType(dto.getDocumentType())
                .dateEntry(LocalDateTime.parse(dto.getDateEntry(), formatter))
                .departureDate(LocalDateTime.parse(dto.getDepartureDate(), formatter))
                .room(room)
                .build();

        return createReservation(reservation);
    }
}
