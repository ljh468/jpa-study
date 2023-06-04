package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQuery(
    name = "Member.findByUsername",
    query = "select m from Member m where m.username= :username"
) // NamedQuery는 애플리케이션 로딩 시점에 파싱을 하기 때문에 실행시점에 오류를 확인할 수 있다.
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member {

  @Id @GeneratedValue
  @Column(name = "member_id")
  private Long id;

  private String username;

  private int age;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  private Team team;

  public Member(String username) {
    this(username, 0);
  }

  public Member(String username, int age) {
    this(username, age, null);
  }

  public Member(String username, int age, Team team) {
    this.username = username;
    this.age = age;
    if (team != null) {
      changeTeam(team);
    }
  }

  /**
   * 연관관계 편의 메소드
   */
  public void changeTeam(Team team) {
    this.team = team;
    team.getMembers().add(this);
  }
}
