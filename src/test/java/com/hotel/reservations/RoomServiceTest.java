package com.hotel.reservations;

import com.hotel.reservations.model.Room;
import com.hotel.reservations.repository.RoomRepository;
import com.hotel.reservations.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

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

}
