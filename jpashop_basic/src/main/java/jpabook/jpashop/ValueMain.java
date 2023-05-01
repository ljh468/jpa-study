package jpabook.jpashop;

import jpabook.jpashop.domain.Member.Address;
import jpabook.jpashop.domain.Member.Member;
import jpabook.jpashop.domain.Member.Period;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
      System.out.println("==================== 임베디드 타입 START ====================");
      /* 임베디드 타입(복합 값 타입) */
      // 값 타입의 실제 인스턴스인 값을 공유하는 것은 위험 (복사해서 사용)
      // 객체의 공유 참조는 피할 수 없다
      // 그래서 값 타입은 불변 객체로 설계 해야한다
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
      System.out.println("==================== 임베디드 타입 END ====================");

      System.out.println("==================== 값 타입의 비교 START ====================");
      /* 값 타입의 비교 */
      // 값 타입은 a.equals(b) 를 사용해서 동등성 비교를 해야한다.
      Address address1 = new Address("city", "street", "zipcode");
      Address address2 = new Address("city", "street", "zipcode");
      System.out.println("address1 == address2 = " + (address1 == address2));
      System.out.println("address1 equals address2 = " + (address1.equals(address2)));

      System.out.println("==================== 값 타입의 비교 END ====================");

      System.out.println("==================== 값 타입 컬렉션 START ====================");
      /* 값 타입 컬렉션 */
      // 값 타입을 하나 이상 저장할 때 사용
      // @ElementCollection, @CollectionTable 사용
      // 값 타입 컬렉션은 영속성 전에(Cascade) + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다.
      Member memberB = new Member();
      memberB.setName("memberB");
      memberB.setHomeAddress(new Address("cityB", "streetB", "zipcodeB"));

      memberB.getFavoriteFoods().add("APPLE");
      memberB.getFavoriteFoods().add("MELON");
      memberB.getFavoriteFoods().add("BANANA");

      memberB.getAddressHistory().add(new Address("old1", "street1", "zipcode1"));
      memberB.getAddressHistory().add(new Address("old2", "street2", "zipcode2"));
      em.persist(memberB);
      em.flush();
      em.clear();
      System.out.println("==================== 값 타입 컬렉션 END ====================");

      System.out.println("==================== 값 타입 컬렉션 START ====================");
      // 소속된 값 타입은 즉시 로딩
      // 값 타입 컬렉션은 모두 지연 로딩
      Member findMemberB = em.find(Member.class, memberB.getId());
      List<Address> addressHistory = findMemberB.getAddressHistory();
      for (Address ad: addressHistory) {
        System.out.println("ad.getCity() = " + ad.getCity());
      }

      Set<String> favoriteFoods = findMemberB.getFavoriteFoods();
      for (String food : favoriteFoods) {
        System.out.println("food = " + food);
      }
      System.out.println("==================== 값 타입 컬렉션 END ====================");

      System.out.println("==================== 값 타입 수정 START ====================");
      /* 값 타입은 불변 객체 */
      // 값 타입은 완전히 새로운 객체로 교체 해줘야 한다
      Address oldAdd = findMemberB.getHomeAddress();
      findMemberB.setHomeAddress(new Address("newCity", oldAdd.getStreet(), oldAdd.getZipcode()));

      // banana -> kiwi
      findMemberB.getFavoriteFoods().remove("BANANA");
      findMemberB.getFavoriteFoods().add("KIWI");

      // old1 -> new1
      findMemberB.getAddressHistory().remove(new Address("old1", "street1", "zipcode1"));
      findMemberB.getAddressHistory().add(new Address("new1", "street1", "zipcode1"));
      em.flush();
      em.clear();
      System.out.println("==================== 값 타입 수정 END ====================");

      /* 값 타입 컬렉션의 제약사항 */
      // 값 타입은 엔티티와 다르게 식별자 개념이 없다
      // 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다.
      // 실무에서는 상황에 따라 값 타입 컬렉션 대신에 일대다 관계를 고려
      // 식별자가 필요하고, 지속해서 값을 추적, 변경해야 한다면 그것은 값 타입이 아닌 엔티티
      // 값 타입은 정말 값 타입이라 판단될 때만 사용


      tx.commit();
    } catch (Exception exception) {
      exception.printStackTrace();
      tx.rollback();
    } finally {
      em.close();
    }
  }
}
