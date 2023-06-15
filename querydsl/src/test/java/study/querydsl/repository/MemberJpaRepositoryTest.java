package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

  @PersistenceContext
  EntityManager em;

  @Autowired
  MemberJpaRepository memberJpaRepository;

  @Test
  void basicTest() {
    // given
    Member member = new Member("member1", 10);
    // when
    memberJpaRepository.save(member);
    // then
    Member findMember = memberJpaRepository.findById(member.getId()).get();
    assertThat(findMember).isEqualTo(member);

    List<Member> result1 = memberJpaRepository.findAll();
    assertThat(result1).containsExactly(member);

    List<Member> result2 = memberJpaRepository.findByUsername("member1");
    assertThat(result2).containsExactly(member);
  }

  @Test
  void basicQuerydslTest() {
    // given
    Member member = new Member("member1", 10);
    // when
    memberJpaRepository.save(member);
    // then
    Member findMember = memberJpaRepository.findById_Querydsl(member.getId()).get();
    assertThat(findMember).isEqualTo(member);

    List<Member> result1 = memberJpaRepository.findAll_Querydsl();
    assertThat(result1).containsExactly(member);

    List<Member> result2 = memberJpaRepository.findByUsername_Querydsl("member1");
    assertThat(result2).containsExactly(member);
  }

  @Test
  void searchTest() {
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
    // condition.setAgeGoe(35);
    // condition.setAgeLoe(40);
    condition.setTeamName("teamB");
    List<MemberTeamDto> result1 = memberJpaRepository.searchByBuilder(condition);
    List<MemberTeamDto> result2 = memberJpaRepository.search(condition);

    // then
    assertThat(result1).extracting("username").containsExactly("member3", "member4");
    assertThat(result2).extracting("username").containsExactly("member3", "member4");
  }
}