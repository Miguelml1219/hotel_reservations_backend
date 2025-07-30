package com.hotel.reservations.service;

import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.repository.ReservationRepository;
import com.hotel.reservations.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterRoomCorrectly(){
        Room room = Room.builder()
                .floor(1)
                .number(1)
                .type("simple")
                .capacity(1)
                .valuePerNight(25.0)
                .build();

        when(roomRepository.findAll()).thenReturn(List.of());
        when(roomRepository.existsById("101")).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room result = roomService.registerRoom(room);

        assertEquals("101", result.getId());
        assertEquals("simple", result.getType());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void noMustMustNotAllowMoreThan10RoomsPerFloor() {

        List<Room> busy = IntStream.range(0,10)
                        .mapToObj(i -> Room.builder().floor(1).build())
                        .collect(Collectors.toList());

        when(roomRepository.findAll()).thenReturn(busy);

        Room newRoom = Room.builder()
                .floor(1)
                .number(11)
                .capacity(1)
                .valuePerNight(25.0)
                .build();

        assertThrows(IllegalArgumentException.class,() -> roomService.registerRoom(newRoom));

    }

    @Test
    void shouldObtainAllRooms() {

        List<Room> list = List.of(new Room(), new Room());
        when(roomRepository.findAll()).thenReturn(list);

        List<Room> result = roomService.obtainAll();
        assertEquals(2, result.size());

    }

    @Test
    void shouldFilterAvailableRoomsByTypeCapacityAndDates(){

        Room room1 = Room.builder()

                .id("101")
                .type("simple")
                .capacity(1)
                .available(true)
                .build();

        Room room2 = Room.builder()

                .id("102")
                .type("simple")
                .capacity(1)
                .available(true)
                .build();

        LocalDateTime entry = LocalDateTime.of(2025,8,1,15,0);
        LocalDateTime exit = LocalDateTime.of(2025,8,3,11,0);

        when(roomRepository.findByTypeAndCapacityGreaterThanEqualAndAvailableTrue("simple",1))
                .thenReturn(List.of(room1,room2));
        when(reservationRepository.findByRoomIdAndDepartureDateAfterAndDateEntryBefore(
                eq("101"), any(), any())).thenReturn(List.of());
        when(reservationRepository.findByRoomIdAndDepartureDateAfterAndDateEntryBefore(
                eq("102"), any(), any())).thenReturn(List.of(Reservation.builder().id(99L).build()));
        List<Room> result = roomService.filterByAvailablesByDate("simple",1, entry, exit);

        assertEquals(1, result.size());
        assertEquals("101", result.get(0).getId());

    }

}
