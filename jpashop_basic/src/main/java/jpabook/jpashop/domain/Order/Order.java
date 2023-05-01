package jpabook.jpashop.domain.Order;

import jpabook.jpashop.domain.BaseEntity.BaseEntity;
import jpabook.jpashop.domain.Delivery.Delivery;
import jpabook.jpashop.domain.Member.Member;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "ORDERS")
public class Order extends BaseEntity {

  @Id @GeneratedValue
  @Column(name = "ORDER_ID")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "MEMBER_ID")
  private Member member;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItem> orderItems = new ArrayList<>();

  private LocalDateTime orderDate;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "DELIVERY_ID")
  private Delivery delivery;

  public Order() {
  }

  /* 연관관계 메서드 */
  public void changeMember(Member member) {
    this.member = member;
    member.getOrders().add(this);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Member getMember() {
    return member;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }
}
