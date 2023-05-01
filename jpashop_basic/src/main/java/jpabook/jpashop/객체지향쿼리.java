package jpabook.jpashop;

import jpabook.jpashop.domain.Member.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class 객체지향쿼리 {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

    try {
      System.out.println("==================== JPQL START ====================");
      /* JPQL */
      // 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
      // JPQL을 한마디로 정의하면 객체 지향 SQL
      String sql = "select m from Member m where m.name like '%kim%'";
      List<Member> memberList = em.createQuery(sql, Member.class)
                                  .getResultList();
      for (Member member : memberList) {
        System.out.println("member.getName() = " + member.getName());
      }
      System.out.println("==================== JPQL END ====================");

      System.out.println("==================== Criteria START ====================");
      /* Criteria */
      // 동적 쿼리를 만들기 위해 사용

      System.out.println("==================== Criteria END ====================");

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