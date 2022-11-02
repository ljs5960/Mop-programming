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
     * @param amount     결재 금액
     * @param customerId 고객 아이디
     * @return 영수증
     */
    public Receipt pay(long amount, Long customerId) {
        Customer customer = customerRepository.findById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException(customerId);
        }
        if(amount < 0 ) {
            throw  new InvalidAmountException(customerId);
        }

        Receipt receipt = new Receipt(customer);

        this.setPointRate(amount);
        receipt.setPointRate(this.pointRate);

        receipt.setAmount(amount);
        receipt.setPoint((long) (amount * this.pointRate)); //적립된 금액

        customer.addPoint((long) (amount * pointRate));

        return receipt;
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
