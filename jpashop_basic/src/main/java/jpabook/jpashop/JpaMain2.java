package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Team;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain2 {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

    try {
      System.out.println(" ================================================================ ");
      /* 프록시 */
      // 객체 저장
      Member member1 = new Member();
      member1.setName("member1");
      em.persist(member1);

      Member member2 = new Member();
      member2.setName("member1");
      em.persist(member2);

      em.flush();
      em.clear();

      // 객체 조회
      Member m1 = em.find(Member.class, member1.getId());
      Member m2 = em.getReference(Member.class, member2.getId());
      System.out.println("m1 = " + m1.getClass());
      System.out.println("m2 = " + m2.getClass());

      System.out.println("m1 == m2 " + (m1.getClass() == m2.getClass()));
      System.out.println("m1 is Member ? " + (m1 instanceof Member));
      System.out.println("m2 is Member ? " + (m2 instanceof Member));
      System.out.println("m1, m2 same instance ? " + (m1.getClass().isInstance(m2)));
      em.flush();
      em.clear();

      // HibernateProxy (DB 조회를 미루는 프록시 엔티티 객체 조회)
      Member referenceMember1 = em.getReference(Member.class, m1.getId());
      System.out.println("referenceMember = " + referenceMember1.getClass());
      System.out.println("referenceMember.getId() = " + referenceMember1.getId());
      System.out.println("referenceMember.getName() = " + referenceMember1.getName());
      em.flush();
      em.clear();


      System.out.println(" ================================================================ ");
      /* 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 해당 엔티티를 반환 */
      // 같은 영속성 컨텍스트에서 가져온 객체는 항상 같아야함
      Member refMember1 = em.getReference(Member.class, member1.getId()); // Proxy
      Member findMember1 = em.find(Member.class, member1.getId()); // Member
      System.out.println("refMember1 = " + refMember1.getClass());
      System.out.println("findMember1 = " + findMember1.getClass());
      System.out.println("findMember1 == refMember1 ? " + (findMember1 == refMember1));
      em.flush();
      em.clear();

      System.out.println(" ================================================================ ");
      /* 영속성 컨텍스트에서 지우면 초기화 되지 않음 */
      // could not initialize proxy
      Member refMember2 = em.getReference(Member.class, member1.getId()); // Proxy

      // 초기화 여부 확인
      System.out.println("before isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember2));
      // refMember2.getName(); // 강제 호출로 초기화
      Hibernate.initialize(refMember2); // 강제 초기화 (JPA 표준은 강제 초기화 없음)
      System.out.println("after isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember2));

      em.detach(refMember2); // clear(), close()
      // System.out.println("refMember2.getName() = " + refMember2.getName());
      // System.out.println("refMember2.getClass() = " + refMember2.getClass());

      System.out.println(" ================================================================ ");
      /* 영속성 컨텍스트에서 지우면 초기화 되지 않음 */





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