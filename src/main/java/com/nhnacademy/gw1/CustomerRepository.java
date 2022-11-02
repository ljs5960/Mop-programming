package com.nhnacademy.gw1;

public interface CustomerRepository {
    Customer findById(Long customerId);
}
