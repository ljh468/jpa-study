package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
// 제공되는 옵션의 차이
// @javax.transaction.Transactional()
public class MemberService {

  private final MemberRepository memberRepository;

  @Autowired
  public MemberService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  /**
   * 회원가입
   */
  @Transactional //변경
  public Long join(Member member) {
    validateDuplicationMember(member);
    memberRepository.save(member);
    return member.getId();
  }

  private void validateDuplicationMember(Member member) {
    // name 은 유니크 제약조건 이므로 validate 할 수 있음
    // findMembers 리스트를 가져오는 이유는? 값이 두개가 들어가 있을 수 있기 때문에?
    // 나라면 exist 로 존재 유무만 확인해서 예외처리 할 것같음 -> test 코드 확인

    // 1
    List<Member> findMembers = memberRepository.findByName(member.getName());
    if (!findMembers.isEmpty()) {
      throw new IllegalStateException("이미 존재하는 회원입니다.");
    }

    // 2
    // boolean result = memberRepository.existsByNameWithLimit(member.getName());
    // if (result) {
    //   throw new IllegalStateException("이미 존재하는 회원입니다.");
    // }
  }

  /**
   * 전체 회원 조회
   */
  @Transactional(readOnly = true)
  public List<Member> findMembers() {
    return memberRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Member findOne(Long memberId) {
    return memberRepository.findOne(memberId);
  }

  /**
   * 회원 수정
   */
  @Transactional
  public void update(Long id, String name) {
    Member member = memberRepository.findOne(id);
    member.setName(name);
  }
}
