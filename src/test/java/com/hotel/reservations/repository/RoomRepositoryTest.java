package com.hotel.reservations.repository;

import com.hotel.reservations.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp(){
        Room room1 = Room.builder()
                .id("101")
                .floor(1)
                .number(1)
                .type("simple")
                .capacity(1)
                .valuePerNight(25.0)
                .available(true)
                .build();
        Room room2 = Room.builder()
                .id("101")
                .floor(1)
                .number(1)
                .type("double")
                .capacity(2)
                .valuePerNight(40.0)
                .available(false)
                .build();
        Room room3 = Room.builder()
                .id("301")
                .floor(3)
                .number(1)
                .type("suite")
                .capacity(3)
                .valuePerNight(80.0)
                .available(true)
                .build();

        roomRepository.saveAll(List.of(room1,room2,room3));
    }

    @Test
    void mustFindAvailableRooms() {
        List<Room> availables = roomRepository.findByAvailableTrue();
        assertEquals(15, availables.size());
    }


    @Test
    void mustFindByTypeAndMinimumCapacity() {
        List<Room> resultado = roomRepository.findByTypeAndCapacityGreaterThanEqualAndAvailableTrue("suite", 3);
        assertEquals(1, resultado.size());
        assertEquals("301", resultado.get(0).getId());
    }


    @Test
    void mustCountRoomsByFloor() {
        long amount = roomRepository.countByFloor(1);
        assertEquals(2, amount);
    }


}
