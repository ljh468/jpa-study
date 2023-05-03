import jpql.Member;
import jpql.Team;

import javax.persistence.*;
import java.util.List;

public class 페치조인 {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    try {
      Team teamA = new Team();
      teamA.setName("TeamA");
      em.persist(teamA);

      Team teamB = new Team();
      teamB.setName("TeamB");
      em.persist(teamB);

      Member memberA = new Member();
      memberA.setUsername("회원1");
      memberA.setAge(20);
      memberA.changeTeam(teamA);
      em.persist(memberA);

      Member memberB = new Member();
      memberB.setUsername("회원2");
      memberB.setAge(20);
      memberB.changeTeam(teamA);
      em.persist(memberB);

      Member memberC = new Member();
      memberC.setUsername("회원2");
      memberC.setAge(30);
      memberC.changeTeam(teamB);
      em.persist(memberC);

      em.flush();
      em.clear();

      System.out.println(" ================================================================================ ");
      /* 페치 조인 */
      // 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능
      String fetch1 = "select m from Member m join fetch m.team";
      List<Member> fetchResult1 = em.createQuery(fetch1, Member.class).getResultList();
      for (Member member : fetchResult1) {
        System.out.println("username = " + member.getUsername() + ", " +
                               "teamName = " + member.getTeam().getName());
      }

      System.out.println(" ================================================================================ ");
      /* 컬렉션 페치 조인 */
      // 일대다 관계 (데이터 중복으로 뻥튀기 발생)
      // JPQL 의 DISTINCT 2가지 기능 제공
      // 1. SQL 에 DISTINCT 를 추가 (완전히 똑같아야만 DISTINCT 가 됨)
      // 2. 애플리케이션에서 엔티티 중복 제거
      String fetch2 = "select distinct t from Team t join fetch t.members";
      List<Team> fetchResult2 = em.createQuery(fetch2, Team.class).getResultList();
      for (Team team : fetchResult2) {
        System.out.println("teamName = " + team.getName() + ", team = " + team);
        for (Member member : team.getMembers()) {
          //페치 조인으로 팀과 회원을 함께 조회해서 지연 로딩 발생 안함
          System.out.println("-> username = " + member.getUsername()+ ", member = " + member);
        }
      }
      System.out.println(" ================================================================================ ");
      /* 페치 조인의 특징 */
      // 연관된 엔티티들을 SQL 한 번으로 조회 - 성능 최적화
      // 엔티티에 직접 적용하는 글로벌 로딩 전략보다 우선함
      // @OneToMany(fetch = FetchType.LAZY) //글로벌 로딩 전략
      // 실무에서 글로벌 로딩 전략은 모두 지연 로딩
      // 최적화가 필요한 곳은 페치 조인 적용

      /* 페치 조인의 한계 */
      /* 객체 그래프는 연관된 객체는 모두 조회하는 것이 설계 목적 */
      // 페치 조인 대상에는 별칭을 주지 않아야 한다 (연쇄적인 join fetch 시에 사용)
      // 둘 이상의 컬렉션은 페치 조인 할 수 없다. (데이터 뻥튀기)
      // 컬렉션을 페치 조인하면 페이징 API(setFirstResults, setMaxResults)를 사용할 수 없다. (일대다 일때 정합성 문제) (DB는 페이징 하지않음)
      // 일대일, 다대일 같은 단일 값 연관 필드들은 페치 조인해도 페이징 가능 (다대일 구조로 바꿔서 페이징 처리)
      // @BatchSize(size = 100) 를 이용할 수 도 있음

      /* 페치 조인은 객체 그래프를 유지할 때 사용하면 효과적 */
      // 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야 하면, 페치 조인 보다는 일반 조인을 사용하고
      // 필요한 데이터들만 조회해서 DTO 로 반환하는 것이 효과적
      
      tx.commit();
      System.out.println(" ================================================================================ ");
    } catch (Exception exception) {
      exception.printStackTrace();
      tx.rollback();
    } finally {
      em.close();
    }
  }
}
