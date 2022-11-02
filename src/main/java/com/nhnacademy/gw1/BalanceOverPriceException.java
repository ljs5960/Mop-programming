package com.nhnacademy.gw1;

public class BalanceOverPriceException extends RuntimeException {
    public BalanceOverPriceException(Long customerId) {
        super("Price is over than balance : " + customerId);
    }
}
