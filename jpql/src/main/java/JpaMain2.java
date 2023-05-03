import jpql.Member;
import jpql.MemberDTO;
import jpql.MemberType;
import jpql.Team;

import javax.persistence.*;
import java.util.List;

public class JpaMain2 {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    try {
      System.out.println(" ================================================================================ ");
      /* 경로 표현식 */
      
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
