package com.hotel.reservations.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Represent a table in DB
@Entity

//Name of the table
@Table(name = "rooms")

//Lombok annotations
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Room {

    @Id
    private String id;

    private Integer floor;

    private Integer number;

    private String type;

    private Integer capacity;

    @Column(name = "value_per_night")
    private Double valuePerNight;

    private Boolean available = true;

    public void setType(String type) {
        this.type = type == null ? null : type.toLowerCase();
    }

}
