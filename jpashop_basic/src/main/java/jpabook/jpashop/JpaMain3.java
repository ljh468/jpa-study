package jpabook.jpashop;

import jpabook.jpashop.domain.Member.Locker;
import jpabook.jpashop.domain.Member.Member;
import jpabook.jpashop.domain.Member.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain3 {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

    try {
      System.out.println(" ================================================================ ");
      /* 즉시로딩, 지연로딩 */
      Team teamA = new Team();
      teamA.setName("teamA");
      em.persist(teamA);

      Team teamB = new Team();
      teamB.setName("teamA");
      em.persist(teamB);

      Locker lockerA = new Locker();
      lockerA.setName("lockerA");
      em.persist(lockerA);

      Locker lockerB = new Locker();
      lockerB.setName("lockerB");
      em.persist(lockerB);

      Member memberA = new Member();
      memberA.setName("member1");
      memberA.setLocker(lockerA);
      memberA.changeTeam(teamA);
      em.persist(memberA);

      Member memberB = new Member();
      memberB.setName("member2");
      memberB.setLocker(lockerB);
      memberB.changeTeam(teamB);
      em.persist(memberB);

      em.flush();
      em.clear();

      // 지연로딩은 프록시 객체를 가져옴
      Member findMemberA = em.find(Member.class, memberA.getId());
      System.out.println("findMember1.getTeam().getClass() = " + findMemberA.getTeam().getClass());
      // 프록시 객체를 사용하는 시점에 초기화
      System.out.println("findMember1.getTeam().getName() = " + findMemberA.getTeam().getName());
      em.flush();
      em.clear();

      // 즉시로딩은 실제 객체를 가져옴
      Member findMemberB = em.find(Member.class, memberB.getId());
      System.out.println("findMember2.getClass() = " + findMemberB.getClass());
      System.out.println("findMember2.getLocker().getClass() = " + findMemberB.getLocker().getClass());
      System.out.println("findMember2.getLocker().getName() = " + findMemberB.getLocker().getName());
      em.flush();
      em.clear();

      System.out.println(" ================================================================ ");
      /* 지연로딩시 JPQL n+1문제 발생 */
      // 1개의 쿼리가 나갈때 join을 걸지 않고 계속 조회하게 되는 문제
      // 패치조인 이나 엔티티 그래프 기능을 이용하라
      List<Member> members = em.createQuery("select m from Member m join fetch m.team", Member.class)
                               .getResultList();
      // SQL: select * from Member
      // SQL: select * from Team where TEAM_ID = xxx

      tx.commit();
    } catch (Exception exception) {
      exception.printStackTrace();
      tx.rollback();
    } finally {
      em.close();
    }
    emf.close();
  }
}