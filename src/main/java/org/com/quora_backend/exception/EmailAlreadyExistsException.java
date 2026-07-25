package org.com.quora_backend.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String s){
        super(s);
    }
}
