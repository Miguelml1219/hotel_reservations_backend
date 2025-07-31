package com.hotel.reservations.repository;

import com.hotel.reservations.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository <Room, String> {

    List<Room> findByAvailableTrue();
    List<Room> findByTypeAndCapacityGreaterThanEqualAndAvailableTrue(String type, int capacity);
    long countByFloor(Integer floor);

}
