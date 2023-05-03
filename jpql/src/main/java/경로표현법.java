import jpql.Member;
import jpql.Team;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

public class 경로표현법 {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    try {
      Team team = new Team();
      team.setName("teamA");
      em.persist(team);

      Member memberA = new Member();
      memberA.setUsername("memberA");
      memberA.setAge(10);
      memberA.changeTeam(team);
      em.persist(memberA);
      em.flush();
      em.clear();

      System.out.println(" ================================================================================ ");
      /* 경로 표현식 */
      /* 묵시적 조인은 사용하지 않아야 한다. */

      // 상태 필드 : 경로 탐색의 끝, 탐색 X
      String stateField = "select m.username, m.age from Member m";
      List<Object[]> stateResult = em.createQuery(stateField).getResultList();
      for (Object[] objects : stateResult) {
        System.out.println("objects[0] = " + objects[0]);
        System.out.println("objects[1] = " + objects[1]);
      }
      System.out.println(" ================================================================================ ");
      // 단일 값 연관필드 : 묵시적 내부조인 발생, 탐색 O
      String singleField = "select o.member from Order o";
      List<Member> sigleField = em.createQuery(singleField, Member.class).getResultList();
      for (Member member : sigleField) {
        System.out.println("member = " + member);
      }
      System.out.println(" ================================================================================ ");
      // 컬렉션 값 연관필드 : 묵시적 내부조인 발생, 탐색 X
      String collectionField = "select t.members from Team t";
      List<Collection> collectionResult = em.createQuery(collectionField, Collection.class).getResultList();
      for (Object o : collectionResult) {
        System.out.println("o = " + o);
      }
      System.out.println(" ================================================================================ ");
      // 컬렉션 값 연관필드 : 명시적 조인 사용
      String collectionField2 = "select m.username from Team t join t.members m";
      List<String> collectionResult2 = em.createQuery(collectionField2, String.class).getResultList();
      for (String s : collectionResult2) {
        System.out.println("s = " + s);
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
