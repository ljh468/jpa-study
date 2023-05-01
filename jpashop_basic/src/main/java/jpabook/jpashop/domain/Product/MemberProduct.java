package jpabook.jpashop.domain.Product;

import jpabook.jpashop.domain.BaseEntity.BaseEntity;
import jpabook.jpashop.domain.Member.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "MEMBER_PRODUCT")
public class MemberProduct extends BaseEntity {

  @Id @GeneratedValue
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "MEMBER_ID")
  private Member member;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "PROCT_ID")
  private Product product;

  private int count;

  private int price;

  private LocalDateTime orderDateTime;

}
