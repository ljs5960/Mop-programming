package com.nhnacademy.gw1;

public class Receipt {

  private Long price; // 결제 금액
  private Long point; // 적립된 포인트(1000원)
  private double pointRate; // 적립률
  private final Customer customer;


  public void setPoint(Long point) {
    this.point = point;
  }

  public double getPointRate() {
    return pointRate;
  }

  public void setPointRate(double pointRate) {
    this.pointRate = pointRate;
  }

  public Receipt(Customer customer) {
      this.customer = customer;
  }

  public Long getPrice(){
    return this.price;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setPrice(Long price) {
    this.price = price;
  }

  public Long getPoint() {
    return point;
  }

  public void setPoint(long point) {
    this.point=point;
  }
}
