package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.Entity;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
// 생략 가능
// 컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리
// JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리
public interface MemberRepository extends JpaRepository<Member, Long> {

  List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

  // Member에 정의된 NamedQuery를 먼저 찾아서 실행한다.
  // @Query(name = "Member.findByUsername")
  List<Member> findByUsername(@Param("username") String username);

  // NamedQuery처럼 애플리케이션 로딩 시점에 오류를 확인할 수 있다.
  @Query("select m from Member m where m.username= :username and m.age = :age")
  List<Member> findUser(@Param("username") String username, @Param("age") int age);

  @Query("select m.username from Member m")
  List<String> findUsernameList();

  @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, m.age, t.name) from Member m join m.team t")
  List<MemberDto> findMemberDto();

  @Query("select m from Member m where m.username in :names")
  List<Member> findByNames(@Param("names") List<String> names);

  List<Member> findListByUsername(String username);

  Member findMemberByUsername(String username);

  Optional<Member> findOptionalByUsername(String username);

  /**
   * 페이징과 정렬 사용
   */

  // count 쿼리를 다음과 같이 분리할 수 있음
  @Query(value = "select m from Member m join fetch m.team t", countQuery = "select count(m) from Member m")
  Page<Member> findByAge(int age, Pageable pageable);

  Page<Member> findByUsername(String name, Pageable pageable); // count 쿼리 사용

  @Modifying(clearAutomatically = true)
  @Query(value = "update Member m set m.age = m.age + 1 where m.age >= :age")
  int bulkAgePlus(@Param("age") int age);

  /**
   * Fetch Join
   */
  @Query(value = "select m from Member  m left join fetch m.team")
  List<Member> findMemberFetchJoin();

  /**
   * Entity Graph
   */
  // 공통 메서드 오버라이드
  // @Override
  // @EntityGraph(attributePaths = {"team"})
  // List<Member> findAll();

  // JPQL + 엔티티 그래프
  @EntityGraph(attributePaths = {"team"})
  @Query("select m from Member m")
  List<Member> findMemberEntityGraph();

  // 메서드 이름으로 쿼리에서 특히 편리하다.
  @EntityGraph(attributePaths = {"team"})
  List<Member> findEntityGraphByUsername(@Param("username") String username);

  // Named Entity Graph
  @EntityGraph("Member.all")
  @Query("select m from Member m")
  List<Member> findNamedEntityGraphAll();

  @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
  Member findReadOnlyByUsername(String username);

  /**
   * Lock
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Member> findLockByUsername(@Param("username") String username);
}