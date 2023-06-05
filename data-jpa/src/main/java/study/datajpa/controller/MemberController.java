package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberRepository memberRepository;

  @GetMapping("/members/{id}")
  public String findMember(@PathVariable("id") Long id) {
    Member member = memberRepository.findById(id).get();
    return member.getUsername();
  }

  /**
   * 도메인 클래스 컨버터
   */
  @GetMapping("/members2/{id}")
  public String findMember2(@PathVariable("id") Member member) {
    return member.getUsername();
  }

  /**
   * 페이징과 정렬
   * /members?page=0&size=3&sort=id,desc&sort=username,desc
   * page: 현재 페이지, 0부터 시작한다.
   * size: 한 페이지에 노출할 데이터 건수
   * sort: 정렬 조건을 정의한다.
   * 예) 정렬 속성,정렬 속성...(ASC | DESC), 정렬 방향을 변경하고 싶으면 sort 파라미터 추가 ( asc 생략 가능)
   */
  @GetMapping("/members")
  public Page<MemberDto> list(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC)
                           Pageable pageable) {
    Page<Member> page = memberRepository.findAll(pageable);
    page.map(MemberDto::new);
    return page.map(member -> new MemberDto(member.getId(), member.getUsername(), member.getAge()));
  }

  // @PostConstruct
  public void init() {
    for (int i = 0; i < 100; i++) {
      memberRepository.save(new Member("User" + (i+1), (i+1)));
    }
  }
}
