package com.nhnacademy.gw1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    // SUT
    PaymentService service;
    // DOC
    CustomerRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerRepository.class);

        service = new PaymentService(repository);
    }

    @Test
    void pay_notFoundCustomer_thenThrowCustomerNotFoundException() {
        long amount = 10_000L;
        Long customerId = 3423432L;

        when(repository.findById(customerId)).thenReturn(null);

        assertThatThrownBy(() -> service.pay(amount, customerId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Not found customer", customerId.toString());
    }

    @Test
    void pay_invalidAmount_thenThrowInvalidAmountException() {
        // invalidAmount : 음수의 금액
        // 음수의 금액이 들어갔을 때, service에서 throwInvalidAmoundException
        long amount = -10L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId);
        when(repository.findById(customerId)).thenReturn(customer);

        assertThatThrownBy(() -> service.pay(amount, customerId))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("Invalid", customerId.toString());
    }


    @Test
    void pay_validAmount() {
        // 유효한 결제금액 결제 시
        long amount = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId);

        when(repository.findById(customerId)).thenReturn(customer);

        assertThatCode(() -> service.pay(amount, customerId)).doesNotThrowAnyException();
    }

    @Test
    void pay_addPoint() {
        long amount = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId);

        when(repository.findById(customerId)).thenReturn(customer);

        // 결제 시 amount에서 적립률만큼 customer의 point에 쌓임.
        Receipt pay = service.pay(amount, customerId);
        pay.getPoint()
        // 포인트 쌓임 체크.
        assertThat(customer.getPoint() > 0).isTrue();
    }
}