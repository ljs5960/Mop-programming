package com.nhnacademy.gw1;

public class Customer {
    private final Long customerId;
    private Long point; //총 적립금

    private Long balance;

    public Customer(Long customerId) {
        this.customerId = customerId;
        point = 0L;
    }

    public Long addPoint(Long addpoint){
        this.point += addpoint;
        return addpoint;
    }

    public Long getPoint() {
        return this.point;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
