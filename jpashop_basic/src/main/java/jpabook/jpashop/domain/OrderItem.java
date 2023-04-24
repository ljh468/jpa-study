package jpabook.jpashop.domain;

import javax.persistence.*;

@Entity
public class OrderItem {

  @Id @GeneratedValue
  @Column(name = "ORDER_ITEM_ID")
  private Long id;;

  @ManyToOne
  @JoinColumn(name = "ORDER_ID")
  private Order order;

  @ManyToOne
  @JoinColumn(name = "ITEM_ID")
  private Item item;

  private int orderPrice;

  private int count;

  public OrderItem() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Order getOrder() {
    return order;
  }

  public void changeOrder(Order order) {
    this.order = order;
  }

  public Item getItem() {
    return item;
  }

  public void changeItem(Item item) {
    this.item = item;
  }

  public int getOrderPrice() {
    return orderPrice;
  }

  public void setOrderPrice(int orderPrice) {
    this.orderPrice = orderPrice;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}