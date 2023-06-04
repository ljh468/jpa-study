package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

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

  @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
  List<MemberDto> findMemberDto();

  @Query("select m from Member m where m.username in :names")
  List<Member> findByNames(@Param("names") List<String> names);

  List<Member> findListByUsername(String username);

  Member findMemberByUsername(String username);

  Optional<Member> findOptinalByUsername(String username);

}