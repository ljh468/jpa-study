package jpabook.jpashop.domain.Product;

import jpabook.jpashop.domain.BaseEntity.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product extends BaseEntity {

  @Id @GeneratedValue
  @Column(name = "PROCT_ID")
  private Long id;

  private String name;

  // @ManyToMany(mappedBy = "products")
  // private List<Member> members = new ArrayList<>();

  @OneToMany(mappedBy = "product")
  private List<MemberProduct> memberProducts = new ArrayList<>();

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
