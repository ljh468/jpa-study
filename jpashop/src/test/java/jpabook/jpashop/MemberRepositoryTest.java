package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
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
    Long saveId = memberRepository.save(member);

    Member findMember = memberRepository.find(saveId);

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getName()).isEqualTo(member.getName());
    assertThat(findMember).isEqualTo(member); // JPA 엔티티 동일성
  }
}