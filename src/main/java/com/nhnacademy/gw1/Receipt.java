package com.nhnacademy.gw1;

public class Receipt {
  private Long originalPrice; // 총 결제 금액
  private Long point; // 적립된 포인트(1000원)
  private double pointRate; // 적립률
  private final Customer customer;
  private Long spentPoint; // 사용한 포인트
  private Long spentBalance; // 사용한 돈

  public Long getSpentPoint() {
    return spentPoint;
  }

  public void setSpentPoint(Long spentPoint) {
    this.spentPoint = spentPoint;
  }

  public Long getSpentBalance() {
    return spentBalance;
  }

  public void setSpentBalance(Long spentBalance) {
    this.spentBalance = spentBalance;
  }

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

  public Long getOriginalPrice(){
    return this.originalPrice;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setOriginalPrice(Long originalPrice) {
    this.originalPrice = originalPrice;
  }

  public Long getPoint() {
    return point;
  }
}
