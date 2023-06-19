package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberTestRepositoryTest {

  @PersistenceContext
  EntityManager em;

  @Autowired
  MemberTestRepository memberTestRepository;

  @Test
  void Querydsl5RepositorySupport_Simple() {
    // given
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

    // when
    MemberSearchCondition condition = new MemberSearchCondition();
    PageRequest pageRequest = PageRequest.of(0, 3);

    System.out.println(" ============================================================= ");
    Page<Member> result = memberTestRepository.applyPagination(condition, pageRequest);

    // then
    assertThat(result.getSize()).isEqualTo(3);
    assertThat(result.getContent()).extracting("username")
                                   .containsExactly("member1", "member2", "member3");
  }

  @Test
  void Querydsl5RepositorySupport_Complex() {
    // given
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

    // when
    MemberSearchCondition condition = new MemberSearchCondition();
    PageRequest pageRequest = PageRequest.of(0, 3);

    System.out.println(" ============================================================= ");
    Page<Member> result = memberTestRepository.applyPagination2(condition, pageRequest);

    // then
    assertThat(result.getSize()).isEqualTo(3);
    assertThat(result.getContent()).extracting("username")
                                   .containsExactly("member1", "member2", "member3");
  }
}