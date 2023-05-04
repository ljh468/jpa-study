import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class 벌크연산 {
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
      memberA.changeTeam(teamA);
      em.persist(memberA);

      Member memberB = new Member();
      memberB.setUsername("회원2");
      memberB.changeTeam(teamA);
      em.persist(memberB);

      Member memberC = new Member();
      memberC.setUsername("회원2");
      memberC.changeTeam(teamB);
      em.persist(memberC);

      em.flush();
      em.clear();

      System.out.println(" ================================================================================ ");
      /* JPQL - 벌크 연산 */
      // 스프링 데이터 JPA 에서 @Modifying
      // 쿼리 한 번으로 여러 테이블 로우 변경(엔티티)
      // executeUpdate()의 결과는 영향받은 엔티티 수 반환
      // UPDATE, DELETE 지원

      // 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리를 날림 (영속성 컨텍스트와 동기화 되지 않음)
      // 벌크 연산을 먼저 실행하거나
      // 벌크 연산 수행 후 영속성 컨텍스트 초기화 해야함

      // 자동으로 FLUSH 호출 (commit, flush, query 시에 DB에 반영)
      // clear 가 되지 않으면 영속성컨텍스트는 남아있음
      int resultCount = em.createQuery("update Member m set m.age = 20")
                          .executeUpdate();
      System.out.println("resultCount = " + resultCount);
      System.out.println("memberA.getAge() = " + memberA.getAge());
      System.out.println("memberB.getAge() = " + memberB.getAge());
      System.out.println("memberC.getAge() = " + memberC.getAge());

      // 벌크 연산 수행후에는 영속성 컨텍스트를 초기화해야 벌크연산 한 내용이 반영됨
      em.clear();
      Member findMember = em.find(Member.class, memberA.getId());
      System.out.println("findMember = " + findMember);


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
