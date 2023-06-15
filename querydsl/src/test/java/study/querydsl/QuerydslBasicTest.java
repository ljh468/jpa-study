package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;
import static com.querydsl.jpa.JPAExpressions.select;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

  @PersistenceContext EntityManager em;

  @PersistenceUnit EntityManagerFactory emf;

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

  /**
   * 회원 정렬 순서
   * 1. 회원 나이 내림차순(desc)
   * 2. 회원 이름 오름차순(asc)
   * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
   */
  @Test
  void sort() {
    // given
    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));

    // when
    List<Member> result = queryFactory.selectFrom(member)
                                      .where(member.age.eq(100))
                                      .orderBy(member.age.desc(), member.username.asc().nullsLast())
                                      .fetch();

    // then
    Member member5 = result.get(0);
    Member member6 = result.get(1);
    Member memberNull = result.get(2);
    assertThat(member5.getUsername()).isEqualTo("member5");
    assertThat(member6.getUsername()).isEqualTo("member6");
    assertThat(memberNull.getUsername()).isNull();
  }

  @Test
  public void paging1() {
    List<Member> result = queryFactory.selectFrom(member)
                                      .orderBy(member.username.desc())
                                      .offset(1)
                                      .limit(2)
                                      .fetch();

    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void paging2() {
    QueryResults<Member> queryResults = queryFactory.selectFrom(member)
                                                    .orderBy(member.username.desc())
                                                    .offset(1)
                                                    .limit(2)
                                                    .fetchResults();

    assertThat(queryResults.getTotal()).isEqualTo(4);
    assertThat(queryResults.getLimit()).isEqualTo(2);
    assertThat(queryResults.getOffset()).isEqualTo(1);
    assertThat(queryResults.getResults().size()).isEqualTo(2);
  }

  /**
   * JPQL
   * select
   * COUNT(m), //회원수
   * SUM(m.age), //나이 합
   * AVG(m.age), //평균 나이
   * MAX(m.age), //최대 나이
   * MIN(m.age) //최소 나이
   * from Member m
   */
  @Test
  void aggregation() {
    // given
    List<Tuple> result = queryFactory.select(member.count(),
                                             member.age.sum(),
                                             member.age.avg(),
                                             member.age.max(),
                                             member.age.min())
                                     .from(member)
                                     .fetch();
    // when
    Tuple tuple = result.get(0);

    // then
    assertThat(tuple.get(member.count())).isEqualTo(4);
    assertThat(tuple.get(member.age.sum())).isEqualTo(100);
    assertThat(tuple.get(member.age.avg())).isEqualTo(25);
    assertThat(tuple.get(member.age.max())).isEqualTo(40);
    assertThat(tuple.get(member.age.min())).isEqualTo(10);
  }

  /**
   * 팀의 이름과 각 팀의 평균 연령을 구해라.
   */
  @Test
  void group() {
    // given
    List<Tuple> result = queryFactory.select(team.name, member.age.avg())
                                     .from(member)
                                     .join(member.team, team)
                                     .groupBy(team.name)
                                     .fetch();
    // when
    Tuple teamA = result.get(0);
    Tuple teamB = result.get(1);

    // then
    assertThat(teamA.get(team.name)).isEqualTo("teamA");
    assertThat(teamA.get(member.age.avg())).isEqualTo(15);
    assertThat(teamB.get(team.name)).isEqualTo("teamB");
    assertThat(teamB.get(member.age.avg())).isEqualTo(35);
  }

  /**
   * 팀 A에 소속된 모든 회원
   */
  @Test
  public void join() {
    QMember member = QMember.member;
    QTeam team = QTeam.team;

    List<Member> result = queryFactory.selectFrom(member)
                                      .join(member.team, team)
                                      .where(team.name.eq("teamA"))
                                      .fetch();

    assertThat(result).extracting("username").containsExactly("member1", "member2");
  }

  /**
   * 세타 조인(연관관계가 없는 필드로 조인)
   * 회원의 이름이 팀 이름과 같은 회원 조회
   */
  @Test
  public void theta_join() {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));

    List<Member> result = queryFactory.select(member)
                                      .from(member, team)
                                      .where(member.username.eq(team.name))
                                      .fetch();

    assertThat(result).extracting("username").containsExactly("teamA", "teamB");
  }

  /**
   * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
   * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
   * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='teamA'
   */
  @Test
  void join_on_filtering() {
    // given
    List<Tuple> result = queryFactory.select(member, team)
                                     .from(member)
                                     .leftJoin(member.team, team)
                                     // .on(team.name.eq("teamA"))
                                     .where(team.name.eq("teamA"))
                                     .fetch();

    // when, then
    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }

  /**
   * 2. 연관관계 없는 엔티티 외부 조인
   * 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
   * JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
   * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
   */
  @Test
  void join_on_no_relation() {
    // given
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));

    // when
    List<Tuple> result = queryFactory.select(member, team)
                                     .from(member)
                                     .leftJoin(team)
                                     .on(member.username.eq(team.name))
                                     .fetch();

    // then
    for (Tuple tuple : result) {
      System.out.println("t = " + tuple);
    }
  }

  @Test
  void fetchJoinNo() {
    // given
    em.flush();
    em.clear();

    // when
    Member findMember = queryFactory.selectFrom(member)
                                    .where(member.username.eq("member1"))
                                    .fetchOne();

    // then
    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 미적용").isFalse();

  }

  @Test
  void fetchJoinUse() {
    // given
    em.flush();
    em.clear();

    // when
    Member findMember = queryFactory.selectFrom(member)
                                    .join(member.team, team).fetchJoin()
                                    .where(member.username.eq("member1"))
                                    .fetchOne();

    // then
    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 적용").isTrue();
  }

  /**
   * 나이가 가장 많은 회원을 조회
   */
  @Test
  void subQuery() {
    // given
    QMember memberSub = new QMember("memberSub");

    // when
    List<Member> result = queryFactory.selectFrom(member)
                                      .where(member.age.eq(
                                          JPAExpressions.select(memberSub.age.max())
                                                        .from(memberSub)
                                      ))
                                      .fetch();

    // then
    assertThat(result).extracting("age")
                      .containsExactly(40);
  }

  /**
   * 나이가 평균 나이 이상인 회원
   */
  @Test
  void subQueryGoe() {
    // given
    QMember memberSub = new QMember("memberSub");

    // when
    List<Member> result = queryFactory.selectFrom(member)
                                      .where(member.age.goe(
                                          JPAExpressions.select(memberSub.age.avg())
                                                        .from(memberSub)
                                      ))
                                      .fetch();

    // then
    assertThat(result).extracting("age")
                      .containsExactly(30, 40);
  }

  /**
   * 서브쿼리 여러 건 처리, in 사용
   */
  @Test
  void subQueryIn() {
    // given
    QMember memberSub = new QMember("memberSub");

    // when
    List<Member> result = queryFactory.selectFrom(member)
                                      .where(member.age.in(
                                          JPAExpressions.select(memberSub.age)
                                                        .from(memberSub)
                                                        .where(memberSub.age.gt(10))
                                      ))
                                      .fetch();

    // then
    assertThat(result).extracting("age")
                      .containsExactly(20, 30, 40);
  }

  /**
   * select절에 subQuery
   */
  @Test
  void selectSubQuery() {
    // given
    QMember memberSub = new QMember("memberSub");

    // when
    List<Tuple> result = queryFactory.select(member.username,
                                             JPAExpressions.select(memberSub.age.avg())
                                                           .from(memberSub)
                                     ).from(member)
                                     .fetch();

    // then
    for (Tuple tuple : result) {
      System.out.println("username = " + tuple.get(member.username));
      System.out.println("age = " + tuple.get(JPAExpressions
                                                  .select(memberSub.age.avg())
                                                  .from(memberSub)));
    }
  }

  @Test
  void staticSubQuery() {
    // given
    QMember memberSub = new QMember("memberSub");

    // when
    List<Member> result = queryFactory.selectFrom(member)
                                      .where(member.age.eq(
                                          select(memberSub.age.max())
                                              .from(memberSub)
                                      ))
                                      .fetch();
    // then
    assertThat(result).extracting("username")
                      .containsExactly("member4");
  }

  /**
   * 간단한 case문
   */
  @Test
  void basicCase() {
    List<String> result1 = queryFactory.select(member.age
                                                   .when(10).then("열살")
                                                   .when(20).then("스무살")
                                                   .otherwise("기타"))
                                       .from(member)
                                       .fetch();
    for (String s : result1) {
      System.out.println("s = " + s);
    }
  }

  /**
   * 복잡한 case문
   */
  @Test
  void complexCase() {
    // given
    List<String> result = queryFactory
        .select(new CaseBuilder()
                    .when(member.age.between(0, 20)).then("0~20살")
                    .when(member.age.between(21, 30)).then("21~30살")
                    .otherwise("기타"))
        .from(member)
        .fetch();

    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  /**
   * 상수 더하기
   */
  @Test
  void constant() {
    // given
    List<Tuple> result = queryFactory.select(member.username, Expressions.constant("A"))
                                     .from(member)
                                     .fetch();

    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }

  /**
   * 문자 더하기
   */
  @Test
  void concat() {
    // {username}_{age}
    List<String> result = queryFactory.select(member.username
                                                  .concat("_")
                                                  .concat(member.age.stringValue()))
                                      .from(member)
                                      .where(member.username.eq("member1"))
                                      .fetch();

    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  void tupleProjection() {
    // given
    List<Tuple> result = queryFactory.select(member.username, member.age)
                                     .from(member)
                                     .fetch();

    for (Tuple tuple : result) {
      String username = tuple.get(member.username);
      Integer age = tuple.get(member.age);
      System.out.println("username = " + username);
      System.out.println("age = " + age);
    }
  }

  @Test
  void findDtoByJPQL() {
    // given
    em.createQuery(
          "select new study.querydsl.dto.MemberDto(m.username, m.age) " +
              "from Member m", MemberDto.class)
      .getResultList();
    // when

    // then
  }

  /**
   * 프로퍼티 접근 - Setter
   */
  @Test
  void findDtoBySetter() {
    List<MemberDto> result = queryFactory.select(Projections.bean(MemberDto.class,
                                                                  member.username,
                                                                  member.age))
                                         .from(member)
                                         .fetch();

    for (MemberDto memberDto : result) {
      System.out.println("memberDto = " + memberDto);
    }
  }

  /**
   * 필드 직접 접근
   */
  @Test
  void findDtoByField() {
    List<MemberDto> result = queryFactory.select(Projections.fields(MemberDto.class,
                                                                    member.username,
                                                                    member.age))
                                         .from(member)
                                         .fetch();
    for (MemberDto memberDto : result) {
      System.out.println("memberDto = " + memberDto);
    }
  }

  /**
   * 생성자 접근
   * 타입을 맞춰야함
   */
  @Test
  void findDtoByConstructor() {
    List<MemberDto> result = queryFactory
        .select(Projections.constructor(MemberDto.class,
                                        member.username,
                                        member.age))
        .from(member)
        .fetch();
    for (MemberDto memberDto : result) {
      System.out.println("memberDto = " + memberDto);
    }
  }

  /**
   * 별칭이 다를 때
   */
  @Test
  void findUserDto() {
    QMember memberSub = new QMember("memberSub");
    List<UserDto> result = queryFactory
        .select(Projections.fields(UserDto.class,
                                   member.username.as("name"),
                                   ExpressionUtils.as(
                                       JPAExpressions
                                           .select(memberSub.age.max())
                                           .from(memberSub), "age")
                )
        ).from(member)
        .fetch();
    for (UserDto userDto : result) {
      System.out.println("userDto = " + userDto);
    }
  }

  /**
   * QueryProjection
   * 컴파일 오류로 잡을 수 있음
   * 그러나, DTO가 Querydsl에 의존하게 됨
   */
  @Test
  void findDtoByQueryProjection() {
    // given
    List<MemberDto> result = queryFactory.select(new QMemberDto(member.username, member.age))
                                         .from(member)
                                         .fetch();
    for (MemberDto memberDto : result) {
      System.out.println("memberDto = " + memberDto);
    }
  }

  /**
   * 동적 파라미터
   * BooleanBuilder 사용
   */
  @Test
  void dynamicQuery_BooleanBuilder() {
    // given
    String usernameParam = "member1";
    Integer ageParam = null;

    // when
    List<Member> result = searchMember1(usernameParam, ageParam);
    // then
    assertThat(result.size()).isEqualTo(1);
  }

  private List<Member> searchMember1(String usernameCond, Integer ageCond) {
    BooleanBuilder booleanBuilder = new BooleanBuilder();
    if (usernameCond != null) {
      booleanBuilder.and(member.username.eq(usernameCond));
    }
    if (ageCond != null) {
      booleanBuilder.and(member.age.eq(ageCond));
    }
    return queryFactory.selectFrom(member)
                       .where(booleanBuilder)
                       .fetch();
  }

  /**
   * Where 다중 파라미터 사용
   * querydsl의 where조건의 파라미터가 Null이면 무시됨
   */
  @Test
  void dynamicQuery_WhereParam() {
    // given
    String usernameParam = "member1";
    Integer ageParam = null;

    // when
    List<Member> result = searchMember2(usernameParam, ageParam);
    // then
    assertThat(result.size()).isEqualTo(1);
  }

  private List<Member> searchMember2(String usernameCond, Integer ageCond) {
    return queryFactory.selectFrom(member)
                       .where(allEq(usernameCond, ageCond))
                       .fetch();
  }

  private BooleanExpression usernameEq(String usernameCond) {
    return usernameCond != null ? member.username.eq(usernameCond) : null;
  }

  private BooleanExpression ageEq(Integer ageCond) {
    return ageCond != null ? member.age.eq(ageCond) : null;
  }

  private BooleanExpression allEq(String usernameCond, Integer ageCond) {
    return usernameEq(usernameCond).and(ageEq(ageCond));
  }

  /**
   * 수정, 삭제 배치 쿼리
   * 영속성 컨텍스트에 있는 엔티티를 무시하고 실행되기 때문에
   * 배치 쿼리를 실행하고 나면 영속성 컨텍스트를 초기화
   */
  @Test
  void bulkUpdate() {
    // member1 = 10 -> DB 비회원
    // member2 = 20 -> DB 비회원
    // member3 = 30 -> DB 유지
    // member4 = 40 -> DB 유지
    long count = queryFactory.update(member)
                             .set(member.username, "비회원")
                             .where(member.age.lt(28))
                             .execute();

    em.flush();
    em.clear();
    // DB에 적용되더라도 영속성 컨텍스트에서 값을 가져옴 (영속성 초기화 해야함)
    List<Member> result = queryFactory.selectFrom(member)
                                      .fetch();
    for (Member member : result) {
      System.out.println("member = " + member);
    }
  }

  @Test
  void bulkAdd() {
    long count = queryFactory.update(member)
                             .set(member.age, member.age.multiply(2))
                             .execute();
  }

  @Test
  void bulkDelete() {
    long count = queryFactory.delete(member)
                             .where(member.age.gt(18))
                             .execute();
  }

  /**
   * SQL function 호출하기
   * SQL function은 JPA와 같이 Dialect에 등록된 내용만 호출할 수 있음
   */
  @Test
  void sqlFunction() {
    // member -> M으로 변경
    List<String> result = queryFactory.select(Expressions.stringTemplate(
                                          "function('replace', {0}, {1}, {2})",
                                          member.username, "member", "M"))
                                      .from(member)
                                      .fetch();
    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  void sqlFunction2() {
    // 소문자로 변경
    List<String> result = queryFactory.select(member.username)
                                      .from(member)
                                      // .where(member.username.eq(Expressions.stringTemplate(
                                      //     "function('lower', {0}", member.username)))
                                      .where(member.username.eq(member.username.lower()))
                                      .fetch();
    for (String s : result) {
      System.out.println("s = " + s);
    }
  }
}
