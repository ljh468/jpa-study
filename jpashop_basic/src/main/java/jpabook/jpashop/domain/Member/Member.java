package jpabook.jpashop.domain.Member;

import jpabook.jpashop.domain.BaseEntity.BaseEntity;
import jpabook.jpashop.domain.Product.MemberProduct;
import jpabook.jpashop.domain.Order.Order;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseEntity {

  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "MEMBER_ID")
  private Long id;

  private String name;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "LOCKER_ID")
  private Locker locker;

  // @ManyToMany
  // @JoinTable(name = "MEMBER_PRODUCT")
  // private List<Product> products = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private List<MemberProduct> memberProducts = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "TEAM_ID")
  private Team team;

  @OneToMany(mappedBy = "member")
  private List<Order> orders = new ArrayList<>();

  @Embedded
  private Period workPeriod;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "city", column = @Column(name = "HOME_CITY")),
      @AttributeOverride(name = "street", column = @Column(name = "HOME_STREET")),
      @AttributeOverride(name = "zipcode", column = @Column(name = "HOME_ZIPCODE"))
  })
  private Address homeAddress;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "city", column = @Column(name = "WORK_CITY")),
      @AttributeOverride(name = "street", column = @Column(name = "WORK_STREET")),
      @AttributeOverride(name = "zipcode", column = @Column(name = "WORK_ZIPCODE"))
  })
  private Address workAddress;

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

  public Locker getLocker() {
    return locker;
  }

  public void setLocker(Locker locker) {
    this.locker = locker;
  }

  public List<MemberProduct> getMemberProducts() {
    return memberProducts;
  }

  public void setMemberProducts(List<MemberProduct> memberProducts) {
    this.memberProducts = memberProducts;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public Period getWorkPeriod() {
    return workPeriod;
  }

  public void setWorkPeriod(Period workPeriod) {
    this.workPeriod = workPeriod;
  }

  public Address getHomeAddress() {
    return homeAddress;
  }

  public void setHomeAddress(Address homeAddress) {
    this.homeAddress = homeAddress;
  }
}
