package com.nhnacademy.gw1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        // TODO: Keep going~
    }
}