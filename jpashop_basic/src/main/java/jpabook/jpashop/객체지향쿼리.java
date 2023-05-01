package jpabook.jpashop;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Member.Member;
import jpabook.jpashop.domain.Member.QMember;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
      em.flush();
      em.clear();
      System.out.println("==================== JPQL END ====================");

      System.out.println("==================== Criteria START ====================");
      /* Criteria */
      // 동적 쿼리를 만들기 위해 사용
      // 단점: 너무 복잡하고 실용성이 없다.

      // Criteria 사용 준비
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<Member> query = cb.createQuery(Member.class);

      // 루트 클래스 (조회를 시작할 클래스)
      Root<Member> m = query.from(Member.class);

      // 쿼리 생성
      CriteriaQuery<Member> cq = query.select(m).where(cb.like(m.get("name"), "%kim%"));
      List<Member> resultListA = em.createQuery(cq).getResultList();
      em.flush();
      em.clear();
      System.out.println("==================== Criteria END ====================");

      System.out.println("==================== QueryDSL START ====================");
      /* QueryDSL */
      // 동적 쿼리를 만들기 위해 사용
      // 문자가 아닌 자바코드로 JPQL 을 작성할 수 있음
      // 컴파일 시점에 문법 오류를 찾을 수 있음

      // select m from Member m where m.age > 18
      JPAQueryFactory queryFactory = new JPAQueryFactory(em);
      QMember qMember = QMember.member;
      List<Member> MemberDsl = queryFactory.selectFrom(qMember)
                                           .where(qMember.age.gt(18))
                                           .orderBy(qMember.name.desc())
                                           .fetch();
      for (Member member : MemberDsl) {
        System.out.println("member.getName() = " + member.getName());
      }

      System.out.println("==================== QueryDSL END ====================");

      System.out.println("==================== 네이티브 SQL START ====================");
      /* 네이티브 SQL */
      // JPA 가 제공하는 SQL 을 직접 사용하는 기능
      // JPQL 로 해결할 수 없는 특정 데이터베이스에 의존적인 기능 (오라클 CONNECT BY, 특정 DB만 사용하는 SQL 힌트)
      String sqlC = "SELECT MEMBER_ID, AGE, TEAM_ID, NAME FROM MEMBER WHERE NAME = 'kim'";
      List<Member> resultListC = em.createNativeQuery(sqlC, Member.class).getResultList();

      System.out.println("==================== 네이티브 SQL END ====================");

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