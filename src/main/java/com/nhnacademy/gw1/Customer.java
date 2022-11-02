package com.nhnacademy.gw1;

public class Customer {
    private final Long customerId;
    private Long point; //총 적립금

    private Long balance;

    public Customer(Long customerId, Long balance) {
        this.customerId = customerId;
        this.balance = balance;
        this.point = 0L;
    }

    public void addPoint(Long addPoint){
        this.point += addPoint;
    }

    public void subtractPoint(Long subtractPoint){
        this.point -= subtractPoint;
    }

    public Long getPoint() { //총 적립금
        return this.point;
    }

    public Long getBalance() { // 내가 소지하고 있는 돈
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getTotalAmount() {
        return this.point + this.balance;
    }
}
