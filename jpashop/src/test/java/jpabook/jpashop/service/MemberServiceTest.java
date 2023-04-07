package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@Transactional
class MemberServiceTest {

  @Autowired
  MemberService memberService;

  @Autowired
  MemberRepository memberRepository;

  /**
   * 테스트 요구사항
   * 1. 회원가입을 성공해야한다.
   * 2. 같은 이름이 있으면 예외가 발생해야 한다.
   */
  @Test
  @Rollback(value = false)
  void 회원가입에_성공(){
    // given
    Member member = new Member();
    member.setName("lee");

    // when
    Long saveId = memberService.join(member);

    // then
    assertThat(member).isEqualTo(memberRepository.findOne(saveId));
  }

  @Test
  void 중복_회원인_경우_IllegalStateException_예외가_발생한다(){
    // given
    Member member1 = new Member();
    member1.setName("lee1");
    Member member2 = new Member();
    member2.setName("lee1");

    // when
    memberService.join(member1);

    // then
    Assertions.assertThatThrownBy(() -> memberService.join(member2))
              .isInstanceOf(IllegalStateException.class)
              .hasMessage("이미 존재하는 회원입니다.");
  }

}