package hello;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueName", columnNames = {"id", "name"})})
public class Member {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 50, columnDefinition = "varchar(100) default 'EMPTY'")
  private String name;

  @Column(precision = 10, scale = 2)
  private BigDecimal age;

  @Enumerated(EnumType.STRING) // 필수로 EnumType은 String으로 지정
  private RoleType roleType;

  @Temporal(TemporalType.TIMESTAMP) // 최신 하이버네이트는 LocalDate, LocalDateTime 지원
  private Date createdDate;

  private LocalDateTime lastModifiedDate;

  @Lob
  private String description;

  @Transient
  private int temp;

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

  public BigDecimal getAge() {
    return age;
  }

  public void setAge(BigDecimal age) {
    this.age = age;
  }

  public RoleType getRoleType() {
    return roleType;
  }

  public void setRoleType(RoleType roleType) {
    this.roleType = roleType;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public LocalDateTime getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "Member{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", age=" + age +
        ", roleType=" + roleType +
        ", createdDate=" + createdDate +
        ", lastModifiedDate=" + lastModifiedDate +
        ", description='" + description + '\'' +
        '}';
  }
}
