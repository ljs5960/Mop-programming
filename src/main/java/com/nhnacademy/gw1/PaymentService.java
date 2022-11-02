package com.nhnacademy.gw1;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentService {
    private static final double EXPENSIVE_POINTRATE = 0.5;
    private static final double CHEAP_POINTRATE = 0.1;

    private final CustomerRepository customerRepository;
    private double pointRate;

    private NotificationService notificationService;
    private boolean isMessageSent = false;

    private String notificationMessage="결제 성공";

    private boolean usingPoint = false;


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

        Receipt  receipt = new Receipt(customer);
        this.setPointRate(price);
        receipt.setOriginalPrice(price);              // 결제 금액 = 제품 금액
        if (usingPoint) {
            if (price <= customer.getPoint()) {
                customer.subtractPoint(price);
            } else {//가격이 포인트보다 큰 경우
                long deductedPrice = price - customer.getPoint();   // 실제 결제 금액
                customer.subtractPoint(customer.getPoint());
                deductPrice(deductedPrice, customer);
            }
            isMessageSent = sendNotification(notificationMessage);
        } else {
            receipt.setPointRate(this.pointRate);   // 적립률
            receipt.setPoint((long) (price * this.pointRate)); // 적립된 포인트
            receipt.setOriginalPrice(price);   // 결제 금액 = 제품 금액

            customer.addPoint((long) (price * pointRate));
            deductPrice(price, customer);
            isMessageSent = sendNotification(notificationMessage);
        }

        return receipt;
    }


    public boolean sendNotification(String message){
        return notificationService.send(message);
    }

    // Balance 에서 뺄 금액
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


    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public void setUsingPoint(boolean usingPoint) {
        this.usingPoint = usingPoint;
    }
}
