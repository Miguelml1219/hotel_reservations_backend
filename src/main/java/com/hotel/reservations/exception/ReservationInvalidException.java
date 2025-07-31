package com.hotel.reservations.exception;

public class ReservationInvalidException extends RuntimeException {
    public ReservationInvalidException(String message) {
        super(message);
    }
}
