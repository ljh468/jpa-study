package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

  @PersistenceContext EntityManager em;

  JPAQueryFactory queryFactory;

  @BeforeEach
  public void before() {
    queryFactory = new JPAQueryFactory(em);

    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);
  }

  @Test
  void startJPQL() {
    // given
    // member1을 찾아라.
    String qlString = "select m from Member m where m.username = :username";

    // when
    Member findMember = em.createQuery(qlString, Member.class)
                          .setParameter("username", "member1")
                          .getSingleResult();

    // then
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  void startQuerydsl() {
    // given
    // member1을 찾아라.
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    QMember m = new QMember("m");

    // when
    Member findMember = queryFactory.select(m)
                                    .from(m)
                                    .where(m.username.eq("member1")) // 파라미터 바인딩 처리
                                    .fetchOne();

    // then
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void startQuerydsl2() {
    // member1을 찾아라.
    QMember m = member;

    // when
    Member findMember = queryFactory.select(m)
                                    .from(m)
                                    .where(m.username.eq("member1"))
                                    .fetchOne();

    // then
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void startQuerydsl3() {
    // member1을 찾아라.
    // when
    Member findMember = queryFactory.select(member)
                                    .from(member)
                                    .where(member.username.eq("member1"))
                                    .fetchOne();

    // then
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void search() {
    Member findMember = queryFactory.selectFrom(member)
                                    .where(member.username.eq("member1")
                                                          .and(member.age.between(10, 30)))
                                    .fetchOne();
    assertThat(findMember.getUsername()).isEqualTo("member1");

    /**
     * JPQL이 제공하는 모든 검색 조건 제공
     * member.username.eq("member1") // username = 'member1'
     * member.username.ne("member1") //username != 'member1'
     * member.username.eq("member1").not() // username != 'member1'
     * member.username.isNotNull() //이름이 is not null
     * member.age.in(10, 20) // age in (10,20)
     * member.age.notIn(10, 20) // age not in (10, 20)
     * member.age.between(10,30) //between 10, 30
     * member.age.goe(30) // age >= 30
     * member.age.gt(30) // age > 30
     * member.age.loe(30) // age <= 30
     * member.age.lt(30) // age < 30
     * member.username.like("member%") //like 검색
     * member.username.contains("member") // like ‘%member%’ 검색
     * member.username.startsWith("member") //like ‘member%’ 검색
     */
  }

  @Test
  public void searchAndParam() {
    List<Member> result1 = queryFactory.selectFrom(member)
                                       .where(member.username.eq("member1"),
                                              member.age.eq(10))
                                       .fetch();
    assertThat(result1.size()).isEqualTo(1);
  }

  @Test
  void 여러가지_검색조회() {
    // List
    List<Member> fetch = queryFactory.selectFrom(member)
                                     .fetch();
    // 단 건
    Member findMember1 = queryFactory.selectFrom(member)
                                     .where(member.username.eq("member1"))
                                     .fetchOne();

    // 처음 한 건 조회
    Member findMember2 = queryFactory.selectFrom(member)
                                     .fetchFirst();

    // 페이징에서 사용
    // QueryResults<Member> results = queryFactory.selectFrom(member)
    //                                            .fetchResults();
    Pageable pageable = PageRequest.of(0, 10);
    List<Member> results = queryFactory.selectFrom(member)
                                       .offset(pageable.getOffset())
                                       .limit(pageable.getPageSize())
                                       .fetch();

    // count 쿼리로 변경
    long count = queryFactory.selectFrom(member)
                             .fetch().size();
    // .fetchCount();
  }
}
