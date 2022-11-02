package com.nhnacademy.gw1;

public class InvalidPriceException extends RuntimeException{
    public InvalidPriceException(Long customerId) {
        super("Invalid Amount." + customerId);
    }
}
