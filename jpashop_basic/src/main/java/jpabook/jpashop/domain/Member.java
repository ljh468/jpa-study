package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

@Entity
public class Member {

  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "MEMBER_ID")
  private Long id;

  private String name;

  @OneToOne
  @JoinColumn(name = "LOCKER_ID")
  private Locker locker;

  // @ManyToMany
  // @JoinTable(name = "MEMBER_PRODUCT")
  // private List<Product> products = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private List<MemberProduct> memberProducts = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "TEAM_ID")
  private Team team;

  @OneToMany(mappedBy = "member")
  private List<Order> orders = new ArrayList<>();

  private String city;

  private String street;

  private String zipcode;

  public Member() {
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

  public Team getTeam() {
    return team;
  }

  /* 연관관계 편의메서드 */
  public void changeTeam(Team team) {
    this.team = team;
    team.getMembers().add(this);
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getZipcode() {
    return zipcode;
  }

  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }
}