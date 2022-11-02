package com.nhnacademy.gw1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    PaymentService service;
    // DOC
    CustomerRepository repository;
    private final Long CUSTOMER_BALANCE = 100_000L;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerRepository.class);
        service = mock(PaymentService.class);
    }
}

//    @Test
//    void send_notificationSuccess() {
//        // 결제 완료시 알람이 send 되는지 확인.
//        Long price = 10_000L;
//        Long customerId = 3423432L;
//        Customer customer = new Customer(customerId,CUSTOMER_BALANCE);
//
//        when(repository.findById(customerId)).thenReturn(customer);
//        when(service.pay(price, customerId)).thenReturn(new Receipt(customer));
//
//        assertTrue();
//    }
//}