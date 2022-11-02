package com.nhnacademy.gw1;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentService {
    private final CustomerRepository customerRepository;
    private double pointRate;
    private static final double EXPENSIVE_POINTRATE = 0.5;
    private static final double CHEAP_POINTRATE = 0.1;
    private NotificationService notificationService;
    private boolean isMessageSent = true;

    public PaymentService(CustomerRepository customerRepository, NotificationService notificationService) {
        this.customerRepository = customerRepository;
        this.notificationService = notificationService;
    }


    /**
     * 결제처리
     *
     * @param price     결재 금액
     * @param customerId 고객 아이디
     * @return 영수증
     */
    public Receipt pay(long price, Long customerId) {
        Customer customer = customerRepository.findById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException(customerId);
        }
        if(price < 0 ) {
            throw  new InvalidPriceException(customerId);
        }
        if (price > customer.getBalance()) {
            throw new BalanceOverPriceException(customerId);
        }

        Receipt receipt = new Receipt(customer);

        this.setPointRate(price);
        receipt.setPointRate(this.pointRate);   // 적립률

        receipt.setPrice(price);   // 결제 금액 = 제품 금액
        receipt.setPoint((long) (price * this.pointRate)); // 적립된 포인트

        customer.addPoint((long) (price * pointRate));
        deductPrice(price, customer);
        if(!sendNotification("결제 완료 메시지")){
            log.error("메시지 전송 실패");
            isMessageSent = false;
        }

        return receipt;
    }


    public boolean sendNotification(String message){
        return notificationService.send(message);
    }

    private void deductPrice(long price, Customer customer) {
        customer.setBalance(customer.getBalance()-price);
    }

    public double getPointRate() {
        return pointRate;
    }

    public void setPointRate(long amount){
        if(amount >= 50000 ){
            this.pointRate= EXPENSIVE_POINTRATE;
        }else {
            this.pointRate = CHEAP_POINTRATE;
        }
    }

    public boolean isMessageSent() {
        return isMessageSent;
    }
}
