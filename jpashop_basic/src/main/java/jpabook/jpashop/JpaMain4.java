package jpabook.jpashop;

import jpabook.jpashop.domain.Parent.Child;
import jpabook.jpashop.domain.Parent.Parent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain4 {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

    try {
      System.out.println(" ================================================================ ");
      /* 영속성 전이: CASCADE */
      // Parent 를 @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
      // 소유자가 하나일 때만 사용함, 영속성을 전이 시킬 수 있음
      Child childA = new Child();
      childA.setName("childA");

      Child childB = new Child();
      childB.setName("childB");

      Parent parent = new Parent();
      parent.setName("parent");
      parent.addChild(childA);
      parent.addChild(childB);
      em.persist(parent);
      // em.persist(childA); em.persist(childB);

      em.flush();
      em.clear();

      System.out.println(" ================================================================ ");
      /* 고아 객체 */
      // 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능
      // 참조하는 곳이 하나일 때 사용해야 함
      // 특정 엔티티가 개인 소유할 때 사용
      // 부모를 제거하면 자식도 제거됨 (CascadeType.REMOVE 처럼 동작)
      Parent findParent = em.find(Parent.class, parent.getId());
      Child findChildB = em.find(Child.class, childB.getId());
      findParent.getChildList().remove(findChildB);

      System.out.println(" ================================================================ ");
      /* 고아 객체 */

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