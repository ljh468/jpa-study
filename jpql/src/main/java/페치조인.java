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
      // 1. SQL 에 DISTINCT 를 추가
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
