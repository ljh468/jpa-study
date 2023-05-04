import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class jpql활용 {
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
      /* JPQL - 엔티티 직접 사용 */

      // 엔티티를 파라미터로 전달
      String jpql1 = "select m from Member m where m =:member";
      Member findMember1 = em.createQuery(jpql1, Member.class)
                             .setParameter("member", memberA)
                             .getSingleResult();
      System.out.println("findMember1 = " + findMember1);

      System.out.println(" ================================================================================ ");
      // 식별자를 직접 전달
      String jpql2 = "select m from Member m where m.id =:memberId";
      Member findMember2 = em.createQuery(jpql2, Member.class)
                             .setParameter("memberId", memberA.getId())
                             .getSingleResult();
      System.out.println("findMember2 = " + findMember2);

      System.out.println(" ================================================================================ ");
      // 엔티티 직접 사용 - 외래 키 값
      String jpql3 = "select m from Member m where m.team =:team";
      List<Member> members = em.createQuery(jpql3, Member.class)
                               .setParameter("team", teamA)
                               .getResultList();
      for (Member member : members) {
        System.out.println("member = " + member);
      }

      System.out.println(" ================================================================================ ");
      /* JPQL - Named 쿼리 */
      /* 스프링 데이터 JPA 의 @Query()와 동일 */
      // 미리 정의해서 이름을 부여해두고 사용하는 JPQL
      // 정적 쿼리만 가능
      // 어노테이션, XML 에 정의
      // 애플리케이션 로딩 시점에 초기화 후 재사용
      /* 애플리케이션 로딩 시점에 쿼리를 검증 */
      List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                                  .setParameter("username", "회원1")
                                  .getResultList();
      for (Member member : resultList) {
        System.out.println("member = " + member);
      }
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
