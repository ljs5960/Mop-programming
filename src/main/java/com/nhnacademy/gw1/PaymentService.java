package com.nhnacademy.gw1;

public class PaymentService {
    private final CustomerRepository customerRepository;
    private final double pointRate = 0.1;

    public PaymentService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

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
        return null;
    }
}
