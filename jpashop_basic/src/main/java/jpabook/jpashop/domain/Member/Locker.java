package jpabook.jpashop.domain.Member;

import jpabook.jpashop.domain.BaseEntity.BaseEntity;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
public class Locker extends BaseEntity {

  @Id @GeneratedValue
  @Column(name = "LOCKER_ID")
  private Long id;

  private String name;

  @OneToOne(mappedBy = "locker", fetch = LAZY)
  private Member member;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
