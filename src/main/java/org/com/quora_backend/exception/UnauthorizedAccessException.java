package org.com.quora_backend.exception;

public class UnauthorizedAccessException extends RuntimeException{
    public UnauthorizedAccessException(String s){
        super(s);
    }
}
