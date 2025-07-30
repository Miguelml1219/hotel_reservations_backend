package com.hotel.reservations.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 12, nullable = false)
    private String identification;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "date_entry", nullable = false)
    private LocalDateTime dateEntry;

    @Column(name = "departure_date", nullable = false)
    private LocalDateTime departureDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;


}
