package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

  @Autowired
  MemberJpaRepository memberJpaRepository;

  @Autowired
  MemberRepository memberRepository;

  @Test
  void testMember(){
    // given
    Member member = new Member("memberA");
    Member saveMember = memberJpaRepository.save(member);

    // when
    Member findMember = memberJpaRepository.find(saveMember.getId());

    // then
    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member); // JPA 엔티티 동일성 보장
  }

  @Test
  void MemberRepositoryTest(){
    // given
    Member member = new Member("memberA");
    Member saveMember = memberRepository.save(member);

    // when
    Member findMember = memberRepository.findById(saveMember.getId()).get();

    // then
    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member); // JPA 엔티티 동일성 보장
  }
}