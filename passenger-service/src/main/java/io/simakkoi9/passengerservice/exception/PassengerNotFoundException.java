package io.simakkoi9.passengerservice.exception;

public class PassengerNotFoundException extends RuntimeException{
    public PassengerNotFoundException(String message){
        super(message);
    }
}
