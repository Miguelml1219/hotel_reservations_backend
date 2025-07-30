package com.hotel.reservations.service;

import com.hotel.reservations.model.Reservation;
import com.hotel.reservations.model.Room;
import com.hotel.reservations.repository.ReservationRepository;
import com.hotel.reservations.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateReservationIfRoomAvailable(){

        Room room = Room.builder()
                .id("101")
                .type("simple")
                .capacity(1)
                .valuePerNight(25.0)
                .build();

        Reservation reservation = Reservation.builder()
                .name("Miguel")
                .identification("1117350206")
                .documentType("CC")
                .dateEntry(LocalDateTime.now().plusDays(1))
                .departureDate(LocalDateTime.now().plusDays(3))
                .room(room)
                .build();

        when(roomRepository.findById("101")).thenReturn(Optional.of(room));
        when(reservationRepository.findByRoomIdAndDepartureDateAfterAndDateEntryBefore
                (anyString(), any(), any())).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any())).thenReturn(reservation);

        Reservation result  = reservationService.createReservation(reservation);

        assertNotNull(result);
        assertEquals("Miguel", result.getName());
        verify(reservationRepository).save(any());
    }

    @Test
    void shouldReleaseExceptionIfRoomAlreadyHasReservation() {

        Room room = new Room();
        room.setId("101");

        Reservation existingReserve = Reservation.builder()
                .room(room)
                .dateEntry(LocalDateTime.now().plusDays(1))
                .departureDate(LocalDateTime.now().plusDays(3))
                .build();

        Reservation newReservation = Reservation.builder()
                .name("Miguel")
                .identification("1117350206")
                .documentType("CC")
                .dateEntry(LocalDateTime.now().plusDays(2))
                .departureDate(LocalDateTime.now().plusDays(4))
                .room(room)
                .build();

        when(roomRepository.findById("101")).thenReturn(Optional.of(room));
        when(reservationRepository.findByRoomIdAndDepartureDateAfterAndDateEntryBefore
                (eq("101"), any(), any())).thenReturn(List.of(existingReserve));

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(newReservation));
    }



}
