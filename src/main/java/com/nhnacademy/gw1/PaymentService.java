package com.nhnacademy.gw1;

public class PaymentService {
    private final CustomerRepository customerRepository;
    private double pointRate;

    public PaymentService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    private static final double EXPENSIVE_POINTRATE = 0.5;
    private static final double CHEAP_POINTRATE = 0.1;
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

        return receipt;
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
}
