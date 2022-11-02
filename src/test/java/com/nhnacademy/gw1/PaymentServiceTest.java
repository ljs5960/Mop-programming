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
    void pay_success_receipt_addPoint() {
        //할인율이 영수증에 제대로 들어갔는지
        long amount = 10_000L;
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId);

        when(repository.findById(customerId)).thenReturn(customer);

        // 결제 시 amount에서 적립률만큼 customer의 point에 쌓임.
        Receipt receipt = service.pay(amount, customerId);

        //receipt 속의 전체 금액 == amount * 할인율 곱한게 같은지
        assertThat(amount *service.getPointRate() == receipt.getPoint());
    }

    public void pay_success_customer_addPoint(){
        //적립금이 고객의 계정에 제대로 들어갔는지
        long amount = 10_000L;
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId);
        when(repository.findById(customerId)).thenReturn(customer);

        Receipt receipt = service.pay(amount, customerId);

        //고객의 영수증에 point를 넣어줌
        //총 적립금을 제대로 계산이 됬는지(0원 적립도 체크)
        //addPoint를 했을 때 현재 적립금이 제대로 들어갔는지 확인
//        assertThat(customer.addPoint(receipt.getPoint());
)

    }
}