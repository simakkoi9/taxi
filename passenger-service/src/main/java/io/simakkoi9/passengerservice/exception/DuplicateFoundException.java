package io.simakkoi9.passengerservice.exception;

public class DuplicateFoundException extends RuntimeException{
    public DuplicateFoundException(String message){
        super(message);
    }
}
