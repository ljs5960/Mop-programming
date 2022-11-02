package com.nhnacademy.gw1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentServiceTest {
    // SUT
    PaymentService service;

    NotificationService notificationService;
    // DOC
    CustomerRepository repository;

    private final Long CUSTOMER_BALANCE = 100_000L;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerRepository.class);

        notificationService = new SmsNotificationService();

        service = new PaymentService(repository, notificationService);

    }

    @Test
    void pay_notFoundCustomer_thenThrowCustomerNotFoundException() {
        //고객 정보 없음
        Long price = 10_000L;
        Long customerId = 3423432L;

        when(repository.findById(customerId)).thenReturn(null);

        assertThatThrownBy(() -> service.pay(price, customerId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Not found customer", customerId.toString());
    }

    @Test
    void pay_invalidPrice_thenThrowInvalidPriceException() {
        // invalidprice : 음수의 금액
        // 음수의 금액이 들어갔을 때, service에서 throwInvalidAmoundException
        Long price = -10L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);
        when(repository.findById(customerId)).thenReturn(customer);

        assertThatThrownBy(() -> service.pay(price, customerId))
                .isInstanceOf(InvalidPriceException.class)
                .hasMessageContaining("Invalid", customerId.toString());
    }

    @Test
    void pay_validPrice() {
        // 유효한 결제금액 결제 시
        Long price = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        when(repository.findById(customerId)).thenReturn(customer);

        assertThatCode(() -> service.pay(price, customerId)).doesNotThrowAnyException();
    }

    @Test
    void pay_successReceiptAddPoint() {
        //할인율이 영수증에 제대로 들어갔는지
        long price = 10_000L;
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        when(repository.findById(customerId)).thenReturn(customer);

        // 결제 시 price에서 적립률만큼 customer의 point에 쌓임.
        Receipt receipt = service.pay(price, customerId);

        //receipt 속의 전체 금액 == price * 할인율 곱한게 같은지
        assertThat((long) (price * service.getPointRate())).isEqualTo(receipt.getPoint());
    }

    @Test
    public void pay_successCheckPointRate() {
        // 금액에 따라 적립금이 차등 적용되는지
        Long price1 = 10_000L;
        Long customerId1 = 3423432L;

        Customer customer1 = new Customer(customerId1, CUSTOMER_BALANCE);
        when(repository.findById(customerId1)).thenReturn(customer1);

        Receipt receipt1 = service.pay(price1, customerId1);
        //price에 따라서 할인율을 다르게 적용 setPointRate를 통해서

        Long price2 = 50_000L;
        Long customerId2 = 123123L;
        Customer customer2 = new Customer(customerId2, CUSTOMER_BALANCE);
        when(repository.findById(customerId2)).thenReturn(customer2);

        Receipt receipt2 = service.pay(price2, customerId2);

        assertAll("test",
                () -> assertEquals(0.1, receipt1.getPointRate()),
                () -> assertEquals(0.5, receipt2.getPointRate())
        );
    }

    @Test
    public void pay_successCustomerAddPointOneTime() {
        //적립금이 고객의 계정에 제대로 들어갔는지
        Long price = 10_000L;
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);
        when(repository.findById(customerId)).thenReturn(customer);

        Receipt receipt = service.pay(price, customerId);
        assertThat(customer.getPoint()).isEqualTo((long) (price * service.getPointRate()));

        //고객의 영수증에 point를 넣어줌
        //총 적립금을 제대로 계산이 됬는지(0원 적립도 체크)
        //addPoint를 했을 때 현재 적립금이 제대로 들어갔는지 확인
    }

    @Test
    public void pay_successCustomerAddPointTwoTime() {
        // 다중 결제시 적립금이 고객의 계정에 제대로 들어갔는지
        Long price = 10_000L;
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);
        when(repository.findById(customerId)).thenReturn(customer);

        Receipt receipt1 = service.pay(price, customerId);
        Receipt receipt2 = service.pay(price, customerId);

        assertThat(customer.getPoint()).isEqualTo(receipt1.getPoint() + receipt2.getPoint());
    }

    @Test
    void pay_failIfOverBalance_thenBalanceOverPriceException() {
        //물건 가격이 잔고보다 초과된 경우
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        // 리파지토리 when
        when(repository.findById(customerId)).thenReturn(customer);

        // price(물건값) 정해서
        Long price = 100_100L;

        //오버되는지 확인
        assertThatThrownBy(() -> service.pay(price, customerId))
                .isInstanceOf(BalanceOverPriceException.class)
                .hasMessageContaining("Price is over", customerId.toString());
    }

    @Test
    void pay_deductPriceFromBalance() {
        //고객 잔고에서 결제금액이 빠졌는지 확인
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        // 리파지토리 when
        when(repository.findById(customerId)).thenReturn(customer);

        Long price = 10_000L;

        Long balance = customer.getBalance();
        service.pay(price, customerId);

        assertThat(customer.getBalance()).isEqualTo(balance - price);
    }

    @Test
    void pay_notUsingPoint_validateReceipt() {
        // 영수증 검증 ( 제품 금액, 적립률, 적립포인트, 고객의 포인트 )
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        // 리파지토리 when
        when(repository.findById(customerId)).thenReturn(customer);

        Long price = 10_000L;

        Long balance = customer.getBalance();
        Receipt receipt = service.pay(price, customerId);

        assertThat(receipt.getOriginalPrice()).isEqualTo(price);
        assertThat(receipt.getPointRate()).isEqualTo(service.getPointRate());
        assertThat(receipt.getPoint()).isEqualTo((long) (price * service.getPointRate()));
        assertThat(receipt.getCustomer().getPoint()).isEqualTo(customer.getPoint());
    }

    @Test
    void send_SmsNotificationSuccess() {
        // 문자 메세지 전송 성공
        Long price = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        when(repository.findById(customerId)).thenReturn(customer);

        service.setNotificationMessage("결제 성공");
        service.pay(price, customerId);
        assertThat(service.isMessageSent()).isTrue();
    }

    @Test
    void send_SmsNotificationFail() {
        // 문자 메세지 전송 실패
        Long price = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        when(repository.findById(customerId)).thenReturn(customer);

        service.setNotificationMessage("결제 실패");
        service.pay(price, customerId);
        assertThat(service.isMessageSent()).isFalse();
    }

    @Test
    void pay_ifPointIsOverThanPrice_Success() {
        // 결제금액을 적립금과 사용자의 잔고와 합쳐서 사용
        // 1. 포인트 사용 true  & 포인트 잔고가 물건 값보다 클때
        Long price = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        when(repository.findById(customerId)).thenReturn(customer);
        service.pay(price, customerId);
        Long customersPoint = customer.getPoint();
        Long customersBalance = customer.getBalance();
        price = 100L;
        service.setUsingPoint(true);
        Receipt receipt = service.pay(price, customerId);

        assertThat(customer.getTotalAmount()).isEqualTo(customersBalance - (receipt.getOriginalPrice() - customersPoint));
    }

    @Test
    void pay_ifPointIsLessThanPrice_Success() {
        // 2. 포인트 사용 true  & 포인트 잔고가 물건 값보다 작을때
        Long price = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        when(repository.findById(customerId)).thenReturn(customer);
        service.pay(price, customerId);
        Long customersPoint = customer.getPoint();
        Long customersBalance = customer.getBalance();
        service.setUsingPoint(true);
        Receipt receipt = service.pay(price, customerId);

        assertThat(customer.getTotalAmount()).isEqualTo(customersBalance - (receipt.getOriginalPrice() - customersPoint));
    }

    @Test
    void send_EmailNotificationSuccess() {
        //이메일 전송 성공
        notificationService = new EmailNotificationService();

        service = new PaymentService(repository, notificationService);

        Long price = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        when(repository.findById(customerId)).thenReturn(customer);

        service.setNotificationMessage("결제 성공");
        service.pay(price, customerId);
        assertThat(service.isMessageSent()).isTrue();
    }

    @Test
    void send_EmailNotificationFail() {
        //이메일 전송 실패
        notificationService = new EmailNotificationService();

        service = new PaymentService(repository, notificationService);

        Long price = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        when(repository.findById(customerId)).thenReturn(customer);

        service.setNotificationMessage("결제 실패");
        service.pay(price, customerId);
        assertThat(service.isMessageSent()).isFalse();
    }

    @Test
    void send_AppNotificationSuccess() {
        // 앱 푸시 전송 성공
        notificationService = new AppNotificationService();

        service = new PaymentService(repository, notificationService);

        Long price = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        when(repository.findById(customerId)).thenReturn(customer);

        service.setNotificationMessage("결제 성공");
        service.pay(price, customerId);
        assertThat(service.isMessageSent()).isTrue();
    }

    @Test
    void send_AppNotificationFail() {
        // 앱 푸시 전송 실패
        notificationService = new AppNotificationService();

        service = new PaymentService(repository, notificationService);

        Long price = 10_000L;
        Long customerId = 3423432L;
        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        when(repository.findById(customerId)).thenReturn(customer);

        service.setNotificationMessage("결제 실패");
        service.pay(price, customerId);
        assertThat(service.isMessageSent()).isFalse();
    }

    @Test
    void pay_usingPoint_validateReceipt() {
        // 영수증 검증 ( 고객 잔여 포인트, 사용한 돈 )
        Long customerId = 3423432L;

        Customer customer = new Customer(customerId, CUSTOMER_BALANCE);

        // 리파지토리 when
        when(repository.findById(customerId)).thenReturn(customer);

        Long price = 10_000L;

        Long balance = customer.getBalance();
        Receipt receipt1 = service.pay(price, customerId);

        service.setUsingPoint(true);
        Receipt receipt2 = service.pay(price, customerId);

        // 첫 번째 영수증의 고객의 잔여 포인트는 1000
        // 두 번째 영수증의 고객의 잔여 포인트는 0
        assertThat(receipt1.getPoint()).isEqualTo((long) (price * service.getPointRate()));
        assertThat(receipt2.getPoint()).isEqualTo(0L);

        // 첫 번째 영수증의 고객의 사용 금액은 10000
        // 두 번째 영수증의 고객의 사용 금액은 9000
        assertThat(receipt1.getSpentBalance()).isEqualTo(price);
        assertThat(receipt2.getSpentBalance()).isEqualTo(price-receipt1.getPoint());
    }
}
