package io.simakkoi9.ridesservice.exception;

public class RideNotFoundException extends RuntimeException{
    public RideNotFoundException(String message){
        super(message);
    }
}
