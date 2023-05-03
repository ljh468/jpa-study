import jpql.Member;
import jpql.MemberDTO;
import jpql.MemberType;
import jpql.Team;

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
      for (int i = 0; i < 5; i++) {
        Team team = new Team();
        team.setName("team" + i);
        em.persist(team);

        Member memberA = new Member();
        memberA.setUsername("member" + i);
        memberA.setAge(i);
        memberA.setType(MemberType.ADMIN);
        memberA.changeTeam(team);
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
      System.out.println(" ================================================================================ ");

      /* 조인 */
      // 내부 조인 : select m from Member m inner join m.team t
      // 외부 조인 : select m from Member m left outer join m.team t
      // 세타 조인 : select count(m) from Member m, Team t where m.username = t.name
      String jpqlJoin = "select m from Member m, Team t where m.username = t.name";
      em.createQuery(jpqlJoin, Member.class).getResultList();
      System.out.println(" ================================================================================ ");

      /* 조인 - ON절 */
      // 조인 대상 필터링
      em.createQuery("select m from Member m left join m.team t on t.name = 'Team1'", Member.class).getResultList();
      System.out.println(" ================================================================================ ");

      // 연관관계가 없는 엔티티 외부조인 (하이버네이트 5.1부터)
      em.createQuery("select m from Member m left join Team t on m.username = t.name", Member.class).getResultList();
      System.out.println(" ================================================================================ ");

      /* 서브쿼리 */
      // JPA 서브 쿼리 한계
      // SELECT 절 도 가능(하이버네이트에서 지원)
      // JPA 는 WHERE, HAVING 절에서만 서브 쿼리 사용 가능
      // FROM 절의 서브 쿼리는 현재 JPQL 에서 불가능 (하이버네이트6 부터는 FROM 절의 서브쿼리를 지원)

      // 나이가 평균보다 많은 회원
      em.createQuery("select m from Member m where m.age > (select avg(m2.age) from Member m2)").getResultList();
      System.out.println(" ================================================================================ ");

      // 한 건이라도 주문한 고객
      em.createQuery("select m from Member m where (select count(o) from Order o where m = o.member) > 0").getResultList();
      System.out.println(" ================================================================================ ");

      // 팀A 소속인 회원 ([NOT] EXISTS : 서브쿼리에 결과가 존재하면 참)
      em.createQuery("select m from Member m where exists (select t from m.team t where t.name = 'TeamA')").getResultList();
      System.out.println(" ================================================================================ ");

      // 전체 상품 각각의 재고보다 주문량이 많은 주문들 (ALL 모두 만족하면 참)
      em.createQuery("select o from Order o where o.orderAmount > ALL(select p.stockAmount from Product p)").getResultList();
      System.out.println(" ================================================================================ ");

      // 어떤 팀 이든 팀에 소속된 회원 (ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참)
      em.createQuery("select m from Member m where m.team = ANY(select t from Team t)").getResultList();
      System.out.println(" ================================================================================ ");

      /* JPQL 타입표현 */
      // 문자: 'HELLO', 'She''s'
      // 숫자: 10L(Long), 10D(Double), 10F(Float)
      // Boolean: TRUE, FALSE
      // ENUM: jpql.MemberType.Admin (패키지명 포함)
      // 엔티티 타입: TYPE(m) = Member (상속 관계에서 사용)
      em.createQuery("select m.username, 'HELLO', true from Member m where m.type = jpql.MemberType.ADMIN").getResultList();
      System.out.println(" ================================================================================ ");

      /* 조건식 - CASE 식 */
      // 기본 CASE 식
      em.createQuery("select case "
                         + "when m.age <= 10 then '학생요금'"
                         + "when m.age >= 60 then '경로요금'"
                         + "else '일반요금'"
                         + "end from Member m").getResultList();

      // COALESCE: 하나씩 조회해서 null 이 아니면 반환
      // 사용자 이름이 없으면 이름 없는 회원을 반환
      em.createQuery("select coalesce(m.username, '이름 없는 회원') from Member m").getResultList();

      // NULLIF: 두 값이 같으면 null 반환, 다르면 첫번째 값 반환
      // 사용자 이름이 '관리자'면 null 을 반환하고 나머지는 본인의 이름을 반환
      em.createQuery("select nullif(m.username, '관리자') from Member m").getResultList();
      System.out.println(" ================================================================================ ");

      /* JPQL 기본 함수 */
      // 사용자 정의 함수 호출
      // 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록
      // em.createQuery("select function('group_concat', i.name) from Item i").getResultList();

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
