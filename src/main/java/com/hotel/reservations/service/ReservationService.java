package com.hotel.reservations.service;

import com.hotel.reservations.dto.ReservationRequestDTO;
import com.hotel.reservations.exception.InvalidDataException;
import com.hotel.reservations.exception.InvalidDateFormatException;
import com.hotel.reservations.exception.ReservationInvalidException;
import com.hotel.reservations.exception.RoomNotAvailableException;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.repository.ReservationRepository;
import com.hotel.reservations.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
                .orElseThrow(() -> new ReservationInvalidException("Room not found"));


        if (!reservation.getName().matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]{2,40}$")) {
            throw new InvalidDataException("Guest name contains invalid characters.");
        }


        if (reservation.getDepartureDate().isBefore(reservation.getDateEntry())) {

            log.error("Invalid reservation: departure date {} is before entry date {}",
                    reservation.getDepartureDate(), reservation.getDateEntry());

            throw new ReservationInvalidException("Departure date cannot be before entry date.");
        }

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

            throw new RoomNotAvailableException("The room is not available in that date range.");
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

        LocalDateTime dateEntry;
        LocalDateTime departureDate;

        try {
            dateEntry = LocalDateTime.parse(dto.getDateEntry(), formatter);
            departureDate = LocalDateTime.parse(dto.getDepartureDate(), formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException("Invalid date format. Use dd/MM/yyyy HH:mm:ss");
        }

        Reservation reservation = Reservation.builder()
                .name(dto.getName())
                .identification(dto.getIdentification())
                .documentType(dto.getDocumentType())
                .dateEntry(dateEntry)
                .departureDate(departureDate)
                .room(room)
                .build();

        return createReservation(reservation);
    }

    public List<Room> obtainBusyRooms(LocalDateTime entry, LocalDateTime exit){
        log.info("Querying occupied rooms between {} and {}", entry, exit);
        return reservationRepository.findRoomsOccupiedInRange(entry, exit);
    }
}
