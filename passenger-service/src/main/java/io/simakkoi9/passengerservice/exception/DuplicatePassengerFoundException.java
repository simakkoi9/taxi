package io.simakkoi9.passengerservice.exception;

public class DuplicatePassengerFoundException extends RuntimeException{
    public DuplicatePassengerFoundException(String message){
        super(message);
    }
}
