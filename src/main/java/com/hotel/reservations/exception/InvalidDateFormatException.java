package com.hotel.reservations.exception;

public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException(String message) {
        super(message);
    }
}
