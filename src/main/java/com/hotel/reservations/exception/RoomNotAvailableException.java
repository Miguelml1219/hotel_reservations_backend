package com.hotel.reservations.exception;

public class RoomNotAvailableException extends RuntimeException{
    public RoomNotAvailableException(String message){
        super(message);
    }
}
