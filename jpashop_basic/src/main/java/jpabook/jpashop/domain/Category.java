package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {

  @Id @GeneratedValue
  @Column(name = "CATEGORY_ID")
  private Long id;

  @OneToMany(mappedBy = "category")
  private List<CategoryItem> categoryItems = new ArrayList<>();

  private String name;
}
