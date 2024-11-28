package com.techmate.techmate.Exception.DTO;

public class UserNotFoundException  extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
    
}
