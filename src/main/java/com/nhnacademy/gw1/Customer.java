package com.nhnacademy.gw1;

public class Customer {
    private final Long customerId;
    private Long point;
    public Customer(Long customerId) {
        this.customerId = customerId;
        point = 0L;
    }

    public Long getPoint() {
        return this.point;
    }

}
