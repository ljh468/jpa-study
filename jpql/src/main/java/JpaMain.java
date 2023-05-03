import jpql.Member;
import jpql.MemberDTO;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    try {
      System.out.println(" ================================================================================ ");
      for (int i = 0; i < 100; i++) {
        Member memberA = new Member();
        memberA.setUsername("member" + i);
        memberA.setAge(i);
        em.persist(memberA);
      }
      em.flush();
      em.clear();
      System.out.println(" ================================================================================ ");

      /* 반환 타입이 명확할 때 */
      TypedQuery<Member> typedQuery = em.createQuery("select m from Member m where m.username =:username", Member.class);
      typedQuery.setParameter("username", "member1");

      System.out.println(" =========== getResultList() =========== ");
      List<Member> resultListA = typedQuery.getResultList(); // 결과가 하나 이상일때, 결과가 없으면 빈 리스트 반환
      for (Member member : resultListA) {
        System.out.println("member.getUsername() = " + member.getUsername());
      }

      System.out.println(" =========== getSingleList() =========== ");
      Member resultA = typedQuery.getSingleResult(); // 결과가 정확히 하나, 단일 객체 반환, 결과가 없으면 No entity found
      System.out.println("result.getUsername() = " + resultA.getUsername());

      /* 프로젝션 */
      /* 엔티티 프로젝션은 영속성 컨텍스트로 관리 */
      resultA.setAge(20);
      em.flush();
      em.clear();
      System.out.println(" ================================================================================ ");

      /* 반환 타입이 명확하지 않을 때 */
      /* 스칼라 타입 프로젝션 (여러 값 조회) */
      // 패키지 명을 포함한 전체 클래스 명 입력
      // 순서와 타입이 일치하는 생성자 필요
      List<MemberDTO> memberDTOS = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class).getResultList();
      for (MemberDTO memberDTO : memberDTOS) {
        System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
        System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
      }
      em.flush();
      em.clear();
      System.out.println(" ================================================================================ ");

      /* 페이징 API */
      // setFirstResult(int startPosition) : 조회 시작 위치 (0부터 시작)
      // setMaxResults(int maxResult) : 조회할 데이터 수
      //페이징 쿼리
      String jpql = "select m from Member m order by m.age desc";
      List<Member> resultListB = em.createQuery(jpql, Member.class)
                                   .setFirstResult(10)
                                   .setMaxResults(20)
                                   .getResultList();
      for (Member member : resultListB) {
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
