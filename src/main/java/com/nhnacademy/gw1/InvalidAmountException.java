package com.nhnacademy.gw1;

public class InvalidAmountException extends RuntimeException{
    public InvalidAmountException(Long customerId) {
        super("Invalid Amount." + customerId);
    }
}
