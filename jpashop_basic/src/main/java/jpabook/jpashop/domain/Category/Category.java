package jpabook.jpashop.domain.Category;

import jpabook.jpashop.domain.BaseEntity.BaseEntity;
import jpabook.jpashop.domain.Item.Item;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
public class Category extends BaseEntity {

  @Id @GeneratedValue
  @Column(name = "CATEGORY_ID")
  private Long id;

  private String name;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "PARENT_ID")
  private Category parent;

  @OneToMany(mappedBy = "parent")
  private List<Category> child = new ArrayList<>();

  // @OneToMany(mappedBy = "category")
  // private List<CategoryItem> categoryItems = new ArrayList<>();

  /* 실전에서는 다대다 사용하면 안됨 */
  /* 실전에서는 중간테이블이 단순하지 않음 */
  @ManyToMany
  @JoinTable(name = "CATEGORY_ITEM",
      joinColumns = @JoinColumn(name = "CATEGORY_ID"),
      inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
  )
  private List<Item> items = new ArrayList<>();

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

  public Category getParent() {
    return parent;
  }

  public void setParent(Category parent) {
    this.parent = parent;
  }

  public List<Category> getChild() {
    return child;
  }

  public void setChild(List<Category> child) {
    this.child = child;
  }

  // public List<CategoryItem> getCategoryItems() {
  //   return categoryItems;
  // }
  //
  // public void setCategoryItems(List<CategoryItem> categoryItems) {
  //   this.categoryItems = categoryItems;
  // }
}
