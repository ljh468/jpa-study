package jpabook.jpashop;

import jpabook.jpashop.domain.Member.Address;
import jpabook.jpashop.domain.Member.Member;
import jpabook.jpashop.domain.Member.Period;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

public class ValueMain {
  public static void main(String[] args) {
    System.out.println(" ================================================================ ");
    /* 값 타입 */
    // 자바의 기본타입은 절대 공유 X
    int a = 10;
    int b = a;
    a = 20;
    System.out.println("a = " + a);
    System.out.println("b = " + b);
    System.out.println(" ================================================================ ");

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

    try {
      /* 임베디드 타입(복합 값 타입) */
      Period period = new Period(LocalDateTime.of(2023, 1, 1, 0, 0, 0),
                                 LocalDateTime.of(2023, 1, 30, 0, 0, 0));
      Address address = new Address("cityA", "streetA", "zipcodeA");
      Member memberA = new Member();
      memberA.setName("memberA");
      memberA.setWorkPeriod(period);
      memberA.setHomeAddress(address);
      em.persist(memberA);
      em.flush();
      em.clear();

      Member findMemberA = em.find(Member.class, memberA.getId());
      LocalDateTime current = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
      System.out.println("isWork = " + findMemberA.getWorkPeriod().isWork(current));
      em.flush();
      em.clear();

      System.out.println(" ================================================================ ");





      tx.commit();
    } catch (Exception exception) {
      exception.printStackTrace();
      tx.rollback();
    } finally {
      em.close();
    }
  }
}
