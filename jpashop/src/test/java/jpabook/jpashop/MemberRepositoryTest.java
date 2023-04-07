package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Test
  @Transactional
  @Rollback(value = false)
  void testMember() {
    Member member = new Member();
    member.setName("memberA");
    memberRepository.save(member);

    Member findMember = memberRepository.findOne(member.getId());

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getName()).isEqualTo(member.getName());
    assertThat(findMember).isEqualTo(member); // JPA 엔티티 동일성
  }

  @Test
  @Transactional
  void memberA의_Name이_존재하면_true를_반환한다() {
    Member member = new Member();
    member.setName("memberA");
    memberRepository.save(member);

    // limit 방식
    boolean result = memberRepository.existsByNameWithLimit(member.getName());
    assertThat(result).isTrue();

    // count 방식
    boolean result2 = memberRepository.existsByNameWithCount(member.getName());
    assertThat(result2).isTrue();
  }

}