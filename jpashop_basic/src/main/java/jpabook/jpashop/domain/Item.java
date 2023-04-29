package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
// @Inheritance(strategy = InheritanceType.JOINED) // 조인전략
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 단일 테이블 전략
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {

  @Id @GeneratedValue
  @Column(name = "ITEM_ID")
  private Long id;

  private String name;

  private int price;

  private int stockQuantity;

  @ManyToMany(mappedBy = "items")
  private List<Category> categories = new ArrayList<>();

  // @OneToMany(mappedBy = "item")
  // private List<CategoryItem> categoryItems = new ArrayList<>();

  public Item() {
  }

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

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public int getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(int stcokQuantity) {
    this.stockQuantity = stcokQuantity;
  }

  @Override
  public String toString() {
    return "Item{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", price=" + price +
        ", stcokQuantity=" + stockQuantity +
        '}';
  }
}
