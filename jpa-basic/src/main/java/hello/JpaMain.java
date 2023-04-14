package hello;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

    try {
      System.out.println(" =================================================================== ");
      // /* 등록 */
      // Member member = new Member();
      // member.setId(1L);
      // member.setName("HelloA");
      // em.persist(member);

      // /* 조회, 수정 */
      // // 영속성 컨텍스트에서 관리하는 객체는 dirty checking 이 들어감
      // Member findMember = em.find(Member.class, 1L);
      // findMember.setName("HelloJPA");

      // /* 삭제 */
      // Member deleteMember = em.find(Member.class, 2L);
      // em.remove(deleteMember);

      // /* 조건이 있는 단건 조회 */
      // List<Member> result = em.createQuery("select m from Member m ", Member.class)
      //                         .getResultList();

      System.out.println(" =================================================================== ");
      // /* 비영속 */
      // Member member = new Member();
      // member.setId(101L);
      // member.setName("HelloJPA");
      //
      // /* 영속 */
      // em.persist(member);
      // Member findMember = em.find(Member.class, 101L);
      //
      // /* 변경 감지 */
      // findMember.setName("HelloJPA dirty checking");

      System.out.println(" =================================================================== ");


      tx.commit();
    } catch (Exception exception) {
      tx.rollback();
    } finally {
      em.close();
    }
    emf.close();
  }
}
