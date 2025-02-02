package io.simakkoi9.ridesservice.exception;

public class BusyPassengerException extends RuntimeException{
    public BusyPassengerException(String message){
        super(message);
    }
}
