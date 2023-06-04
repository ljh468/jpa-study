package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  TeamRepository teamRepository;

  @PersistenceContext
  EntityManager em;

  @Test
  void testMember() {
    // 프록시 구현체
    System.out.println("memberRepository.getClass() = " + memberRepository.getClass());

    // given
    Member member = new Member("memberA");
    Member saveMember = memberRepository.save(member);

    // when
    Member findMember = memberRepository.findById(saveMember.getId()).get();

    // then
    assertThat(saveMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member); // JPA 엔티티 동일성 보장
  }

  @Test
  void basicCRUD() {
    // given
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");
    memberRepository.save(member1);
    memberRepository.save(member2);

    // 단건 조회 검증
    Member findMember1 = memberRepository.findById(member1.getId()).get();
    Member findMember2 = memberRepository.findById(member2.getId()).get();
    assertThat(findMember1).isEqualTo(member1);
    assertThat(findMember2).isEqualTo(member2);

    // 리스트 조회 검증
    List<Member> all = memberRepository.findAll();
    assertThat(all.size()).isEqualTo(2);

    // 카운트 검증
    long count = memberRepository.count();
    assertThat(count).isEqualTo(2);

    // 삭제 검증
    memberRepository.delete(member1);
    memberRepository.delete(member2);

    long deletedCount = memberRepository.count();
    assertThat(deletedCount).isEqualTo(0);
  }

  @Test
  void findByUsernameAndAgeGreaterThan() {
    // given
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("AAA", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    // when
    List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

    // then
    assertThat(result.get(0).getUsername()).isEqualTo("AAA");
    assertThat(result.get(0).getAge()).isEqualTo(20);
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  void testNamedQuery() {
    // given
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    // when
    List<Member> result = memberRepository.findByUsername("AAA");

    // then
    assertThat(result.get(0).getUsername()).isEqualTo("AAA");
    assertThat(result.get(0).getAge()).isEqualTo(10);
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  void testQuery() {
    // given
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    // when
    List<Member> result = memberRepository.findUser("AAA", 10);

    // then
    assertThat(result.get(0)).isEqualTo(m1);
    assertThat(result.get(0).getUsername()).isEqualTo("AAA");
    assertThat(result.get(0).getAge()).isEqualTo(10);
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  void findUsernameList() {
    // given
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    // when
    List<String> result = memberRepository.findUsernameList();

    // then
    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  void findMemberDto() {
    // given
    Team team = new Team("teamA");
    teamRepository.save(team);

    Member m1 = new Member("AAA", 10);
    m1.changeTeam(team);
    memberRepository.save(m1);

    // when
    List<MemberDto> result = memberRepository.findMemberDto();

    // then
    for (MemberDto memberDto : result) {
      System.out.println("memberDto = " + memberDto);
    }
  }

  @Test
  void findByNames() {
    // given
    Team team = new Team("teamA");
    teamRepository.save(team);

    Member m1 = new Member("AAA", 10);
    m1.changeTeam(team);
    memberRepository.save(m1);

    Member m2 = new Member("BBB", 20);
    m2.changeTeam(team);
    memberRepository.save(m2);

    // when
    List<Member> result = memberRepository.findByNames(List.of("AAA", "BBB"));

    // then
    for (Member member : result) {
      System.out.println("member = " + member);
    }
  }

  @Test
  void returnType() {
    // given
    Team team = new Team("teamA");
    teamRepository.save(team);

    Member m1 = new Member("AAA", 10);
    m1.changeTeam(team);
    memberRepository.save(m1);

    Member m2 = new Member("BBB", 20);
    m2.changeTeam(team);
    memberRepository.save(m2);

    // when, than
    List<Member> aaa = memberRepository.findListByUsername("AAA");
    for (Member member : aaa) {
      System.out.println("member = " + member);
    }

    /*
      단건 조회
      결과 없음: javax.persistence.NoResultException 예외가 발생, null 반환 -> Optional로 처리
      결과가 2건 이상: javax.persistence.NonUniqueResultException 예외가 발생
     */
    Member member = memberRepository.findMemberByUsername("AAA");
    System.out.println("member = " + member);

    Member optionalMember = memberRepository.findOptionalByUsername("AAA").get();
    System.out.println("optionalMember = " + optionalMember);
  }

  @Test
  void paging() {
    // given
    Team teamA = new Team("teamA");
    teamRepository.save(teamA);

    Team teamB = new Team("teamB");
    teamRepository.save(teamB);

    Member member1 = new Member("member1", 10);
    member1.changeTeam(teamA);
    memberRepository.save(member1);

    Member member2 = new Member("member2", 20);
    member2.changeTeam(teamA);
    memberRepository.save(member2);

    Member member3 = new Member("member3", 30);
    member3.changeTeam(teamA);
    memberRepository.save(member3);

    Member member4 = new Member("member4", 40);
    member4.changeTeam(teamB);
    memberRepository.save(member4);

    Member member5 = new Member("member5", 50);
    member5.changeTeam(teamB);
    memberRepository.save(member5);

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

    // when
    Page<Member> page = memberRepository.findByAge(age, pageRequest);

    // then
    System.out.println(" ======================================================== ");
    List<Member> content = page.getContent();
    for (Member member : content) {
      System.out.println("member = " + member);
    }
    System.out.println(" ======================================================== ");

    Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m.getId(), m.getUsername(), m.getAge(), m.getTeam().getName()));
    for (MemberDto memberDto : dtoPage) {
      System.out.println("memberDto = " + memberDto);
    }
    System.out.println(" ======================================================== ");

    assertThat(content.size()).isEqualTo(3); // 조회된 데이터 수
    assertThat(page.getTotalElements()).isEqualTo(5); // 전체 데이터 수
    assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
    assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 번호
    assertThat(page.isFirst()).isTrue(); // 첫번째 항목인가?
    assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는가?
  }

  @Test
  void bulkUpdate() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 19));
    memberRepository.save(new Member("member3", 20));
    memberRepository.save(new Member("member4", 21));
    memberRepository.save(new Member("member5", 40));

    // when
    // 벌크성 쿼리를 실행하면 영속성 컨텍스트가 초기화되지 않음
    int resultCount = memberRepository.bulkAgePlus(20);

    // 영속성 컨텍스트가 남아있을 때 다시 조회해야 하면 꼭 영속성 컨텍스트를 초기화
    // @Modifying(clearAutomatically = true)

    List<Member> result = memberRepository.findByUsername("member5");
    Member member5 = result.get(0);
    System.out.println("member5 = " + member5);

    // then
    assertThat(resultCount).isEqualTo(3);
  }

  @Test
  public void findMemberLazy() throws Exception {
    // given
    // member1 -> teamA
    // member2 -> teamB
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);

    memberRepository.save(new Member("member1", 10, teamA));
    memberRepository.save(new Member("member2", 20, teamB));
    em.flush();
    em.clear();

    // when, then
    System.out.println(" ==================================================== ");
    // 바로 조회하면 N+1 문제 발생
    List<Member> members = memberRepository.findAll();
    for (Member member : members) {
      System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
      System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
    }
    em.flush();
    em.clear();

    System.out.println(" ==================================================== ");
    // Fetch Join
    List<Member> memberFetchJoin = memberRepository.findMemberFetchJoin();
    for (Member member : memberFetchJoin) {
      System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
      System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
    }
    em.flush();
    em.clear();

    System.out.println(" ==================================================== ");
    // Entity Graph
    List<Member> memberEntityGraph = memberRepository.findMemberEntityGraph();
    for (Member member : memberEntityGraph) {
      System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
      System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
    }
    em.flush();
    em.clear();
  }

  @Test
  void queryHint() {
    // given
    memberRepository.save(new Member("member1", 10));
    em.flush();
    em.clear();

    // when
    // QueryHint를 readOnly로 설정시 Update Query 실행 X (변경 감지 X)
    Member member = memberRepository.findReadOnlyByUsername("member1");
    member.setUsername("member2");
    em.flush();
  }

  @Test
  void lock() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member1", 20));
    em.flush();
    em.clear();

    // when
    // QueryHint를 readOnly로 설정시 Update Query 실행 X (변경 감지 X)
    List<Member> members = memberRepository.findLockByUsername("member1");
    for (Member member : members) {
      System.out.println("member = " + member);
    }
  }

  @Test
  void callCustom() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member1", 20));
    em.flush();
    em.clear();

    // when
    List<Member> result = memberRepository.findMemberCustom();

    // then
    for (Member member : result) {
      System.out.println("member = " + member);
    }
  }

  @Test
  void jpaEventBaseEntity() throws InterruptedException {
    // given
    Member member = new Member("member1");
    memberRepository.save(member);

    Thread.sleep(100);
    member.setUsername("member2");
    em.flush();
    em.clear();
    
    // when
    Member findMember = memberRepository.findById(member.getId()).get();

    // then
    System.out.println("findMember.getCreatedDate() = " + findMember.getCreatedDate());
    System.out.println("findMember.getUpdatedDate() = " + findMember.getLastModifiedDate());
    System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
    System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());
  }

}
