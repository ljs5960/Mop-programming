package com.nhnacademy.gw1;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long customerId) {
        super("Not found customer: " + customerId);
    }
}
