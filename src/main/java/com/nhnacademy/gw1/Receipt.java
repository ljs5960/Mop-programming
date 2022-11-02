package com.nhnacademy.gw1;

public class Receipt {

  private Long amount; //전체 금액
  private Long point; //적립된 금액(1000원)
  private final Customer customer;

  public Receipt(Customer customer){
    this.customer = customer;
  }

  public Long getAmount(){
    return this.amount;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  public Long getPoint() {
    return point;
  }

  public void setPoint(Long point) {
    this.point = point;
  }
}
