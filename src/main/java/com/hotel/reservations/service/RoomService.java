package com.hotel.reservations.service;

import com.hotel.reservations.exception.EntityNotFoundException;
import com.hotel.reservations.exception.InvalidDataException;
import com.hotel.reservations.exception.MaxMaximumRoomsByFloorException;
import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.repository.ReservationRepository;
import com.hotel.reservations.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RoomService {

    private RoomRepository roomRepository;
    private ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    public Room registerRoom(Room room){

        if(room.getNumber() < 1 || room.getNumber() > 99){
            throw new InvalidDataException("The room number must be between 1 and 99.");
        }

        if(room.getValuePerNight() == null || room.getValuePerNight() <=0){
            throw new InvalidDataException("The value per night must be greater than 0.");
        }

        if(room.getCapacity() == null || room.getCapacity() <=0){
            throw new InvalidDataException("The capacity must be greater than 0.");
        }

        long roomsInFloor = roomRepository.findAll().stream()
                .filter(r -> r.getFloor().equals(room.getFloor()))
                .count();

        if(roomsInFloor >= 10){
            log.error("Error: there are already 10 rooms on the floor {}", room.getFloor());
            throw new MaxMaximumRoomsByFloorException("No more than 10 rooms can be registered per floor");
        }

        String numFormated = String.format("%02d", room.getNumber());

        String idGenerated = room.getFloor().toString() + numFormated;

        if(roomRepository.existsById(idGenerated)){
            throw new IllegalArgumentException("A room with that ID already exists.");
        }

        log.info("Registering room: Floor {}, Number {}, Type {}, Capacity {}, Value ${}",
                room.getFloor(), room.getNumber(), room.getType(),
                room.getCapacity(), room.getValuePerNight());

        room.setId(idGenerated);

        log.info("Room successfully registered with ID: {}", room.getId());

        return roomRepository.save(room);

    }

    public List<Room> obtainAll(){
        log.info("Getting all registered rooms.");
        return roomRepository.findAll();
    }

    public List<Room> obtainAvailables(){
        log.info("Getting available rooms.");
        return roomRepository.findByAvailableTrue();
    }

    public List<Room> filterByAvailables(String type, int minimumCapacity){
        log.info("Filtering available rooms by type ‘{}’ and minimum capacity {}", type, minimumCapacity);
        return roomRepository.findByTypeAndCapacityGreaterThanEqualAndAvailableTrue(type.toLowerCase(), minimumCapacity);
    }

    public Optional<Room> obtainById(String id){
        log.info("Searching for room by ID: {}", id);
        return roomRepository.findById(id);
    }

    public void deleteRoom(String id){
        log.info("Eliminating room with ID: {}", id);
        roomRepository.deleteById(id);
    }

    public Room updateRoom(String id, Room roomUpdate){

        log.info("Updating room with ID: {}", id);

        return roomRepository.findById(id).map(room -> {
            room.setFloor(roomUpdate.getFloor());
            room.setNumber(roomUpdate.getNumber());
            room.setType(roomUpdate.getType());
            room.setCapacity(roomUpdate.getCapacity());
            room.setValuePerNight(roomUpdate.getValuePerNight());
            room.setAvailable(roomUpdate.getAvailable());

            log.info("Room updated: Floor {}, Number {}, Type {}, Capacity {}, Value ${}, Available {}",
                    room.getFloor(), room.getNumber(), room.getType(), room.getCapacity(),
                    room.getValuePerNight(), room.getAvailable());

            return roomRepository.save(room);

        }).orElseThrow(()->{
            log.warn("No room found with ID: {}", id);
            return new EntityNotFoundException("Room not found with ID: "+id);
        });
    }

    public void markNotAvailable(String id){
        log.info("Marking room as unavailable, ID: {}", id);
        Optional<Room> room = roomRepository.findById(id);
        room.ifPresent(r ->{
            r.setAvailable(false);
            roomRepository.save(r);
            log.info("Room with ID {} marked as unavailable.", id);
        });

        if (room.isEmpty()) {
            log.warn("No room found with ID {} to mark as unavailable.", id);
        }
    }


    public List<Room> filterByAvailablesByDate(String type, int minimumCapacity,
                                               LocalDateTime entry, LocalDateTime exit) {
        log.info("Filtering available rooms - Type: {}, Minimum capacity: {}, Dates: {} - {}",
                type, minimumCapacity, entry, exit);

        List<Room> availables = roomRepository.
                findByTypeAndCapacityGreaterThanEqualAndAvailableTrue(type, minimumCapacity);

        return availables.stream()
                .filter(r -> {
                   List<Reservation> overlappingBookings = reservationRepository
                           .findByRoomIdAndDepartureDateAfterAndDateEntryBefore(
                                   r.getId(),entry,exit);
                   return overlappingBookings.isEmpty();
                })
                .toList();

    }
}
