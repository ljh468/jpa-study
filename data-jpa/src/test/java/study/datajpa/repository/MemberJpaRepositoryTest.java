package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

  @Autowired
  MemberJpaRepository memberJpaRepository;

  @Autowired
  MemberRepository memberRepository;

  @PersistenceContext
  EntityManager em;

  @Test
  void testMember() {
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
  public void basicCRUD() {
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");

    memberJpaRepository.save(member1);
    memberJpaRepository.save(member2);

    // 단건 조회 검증
    Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
    Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
    assertThat(findMember1).isEqualTo(member1);
    assertThat(findMember2).isEqualTo(member2);

    // 리스트 조회 검증
    List<Member> all = memberJpaRepository.findAll();
    assertThat(all.size()).isEqualTo(2);

    // 카운트 검증
    long count = memberJpaRepository.count();
    assertThat(count).isEqualTo(2);

    // 삭제 검증
    memberJpaRepository.delete(member1);
    memberJpaRepository.delete(member2);

    long deletedCount = memberJpaRepository.count();
    assertThat(deletedCount).isEqualTo(0);
  }

  @Test
  void findByUsernameAndAgeGreaterThan() {
    // given
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("AAA", 20);
    memberJpaRepository.save(m1);
    memberJpaRepository.save(m2);

    // when
    List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

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
    memberJpaRepository.save(m1);
    memberJpaRepository.save(m2);

    // when
    List<Member> result = memberJpaRepository.findByUsername("AAA");

    // then
    assertThat(result.get(0).getUsername()).isEqualTo("AAA");
    assertThat(result.get(0).getAge()).isEqualTo(10);
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  void paging() {
    // given
    int age = 10;
    int offset = 1;
    int limit = 3;
    memberJpaRepository.save(new Member("member1", 10));
    memberJpaRepository.save(new Member("member2", 10));
    memberJpaRepository.save(new Member("member3", 10));
    memberJpaRepository.save(new Member("member4", 10));
    memberJpaRepository.save(new Member("member5", 10));

    // when
    List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
    long totalCount = memberJpaRepository.totalCount(age);
    // 페이지 계산 공식 적용...
    // totalPage = totalCount / size ...
    // 마지막 페이지 ...
    // 최초 페이지 ..

    // then
    assertThat(members.size()).isEqualTo(3);
    assertThat(totalCount).isEqualTo(5);
  }

  @Test
  void bulkUpdate(){
    // given
    memberJpaRepository.save(new Member("member1", 10));
    memberJpaRepository.save(new Member("member2", 19));
    memberJpaRepository.save(new Member("member3", 20));
    memberJpaRepository.save(new Member("member4", 21));
    memberJpaRepository.save(new Member("member5", 40));

    // when
    // 벌크성 쿼리를 실행하면 영속성 컨텍스트가 초기화되지 않음
    int resultCount = memberRepository.bulkAgePlus(20);

    // 영속성 컨텍스트가 남아있을 때 다시 조회해야 하면 꼭 영속성 컨텍스트를 초기화
    // 영속성 컨텍스트 초기화
    em.flush();
    em.clear();

    /**
     * 권장 방법
     * 1. 영속성 컨텍스트에 엔티티가 없는 상태에서 벌크 연산을 먼저 실행한다.
     * 2. 부득이하게 영속성 컨텍스트에 엔티티가 있으면 벌크 연산 직후 영속성 컨텍스트를 초기화 한다.
     */
    List<Member> result = memberRepository.findByUsername("member5");
    Member member5 = result.get(0);
    System.out.println("member5 = " + member5);

    // then
    assertThat(resultCount).isEqualTo(3);
  }

}