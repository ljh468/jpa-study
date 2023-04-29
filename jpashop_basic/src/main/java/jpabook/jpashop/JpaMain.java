package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Movie;
import jpabook.jpashop.domain.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

    try {
      System.out.println(" ================================================================ ");
      /* 단방향 연관관계 매핑 */
      // 팀 저장
      Team teamA = new Team();
      teamA.setName("TeamA");
      em.persist(teamA); // teamId 생성

      // 회원 저장
      Member memberA = new Member();
      memberA.setName("memberA");
      memberA.changeTeam(teamA); // team의 PK를 꺼내서 FK로 사용
      em.persist(memberA);

      em.flush();
      em.clear();

      // 조회
      Member findMemberA = em.find(Member.class, memberA.getId());
      System.out.println("findMemberA.getName() = " + findMemberA.getName());
      System.out.println("findMemberA.getTeam().getName() = " + findMemberA.getTeam().getName());

      // 참조를 사용해서 연관관계 조회
      Team findTeamA = findMemberA.getTeam();
      System.out.println("findTeamA.getName() = " + findTeamA.getName());

      System.out.println(" ================================================================ ");
      /* 팀 수정 */
      // 새로운 팀B
      Team teamAA = new Team();
      teamAA.setName("TeamAA");
      em.persist(teamAA);

      // 회원1에 새로운 팀B 설정
      findMemberA.changeTeam(teamAA);

      em.flush();
      em.clear();

      Member setMemberA = em.find(Member.class, memberA.getId());
      System.out.println("setMemberA.getName() = " + setMemberA.getName());
      System.out.println("setMemberA.getTeam().getName() = " + setMemberA.getTeam().getName());

      System.out.println(" ================================================================ ");
      /* 양방향 연관관계 매핑 */
      List<Member> memberAs = setMemberA.getTeam().getMembers();
      for (Member m : memberAs) {
        System.out.println("teamA.getMembers -> member.getName() = " + m.getName());
        System.out.println("teamA.getMembers -> member.getTeam().getName() = " + m.getTeam().getName());
      }

      System.out.println(" ================================================================ ");
      /* 양방향 매핑시 주의점 */
      /* 양방향 매핑시 연관관계의 주인에 값을 입력해야한다. */
      Team teamB = new Team();
      teamB.setName("TeamB");
      em.persist(teamB);

      Member memberB = new Member();
      memberB.setName("memberB");

      // 연관관계의 주인에 값을 설정해야함
      memberB.changeTeam(teamB);
      em.persist(memberB);

      // teamB.getMembers().add(memberB); // 읽기 전용 (순수 객체 상태를 고려해서 항상 양쪽에 값을 설정하자)
      // em.flush();
      // em.clear();

      Member findMemberB = em.find(Member.class, memberB.getId());
      System.out.println("findMemberB.getName() = " + findMemberB.getName());

      // teamB.getMembers().add(memberB);을 해주지 않으면 값이 조회되지 않음 (연관관계 편의 메서드 활용)
      Team findTeamB = em.find(Team.class, teamB.getId());
      List<Member> memberBs = findTeamB.getMembers();
      for (Member m : memberBs) {
        System.out.println("findTeamB.getMembers() -> member.getName = " + m.getName());
        System.out.println("findTeamB.getMembers() -> member.getTeam = " + m.getTeam().getName());
      }

      System.out.println(" ================================================================ ");
      /* 고급 매핑(상속관계) */
      Movie movie = new Movie();
      movie.setDirector("director");
      movie.setActor("actor");
      movie.setName("바람과 함께 사라지다");
      movie.setPrice(10000);
      em.persist(movie);

      em.flush();
      em.clear();

      Movie findMovie = em.find(Movie.class, movie.getId());
      System.out.println("findMovie = " + findMovie);

      System.out.println(" ================================================================ ");

      tx.commit();
    } catch (Exception exception) {
      tx.rollback();
    } finally {
      em.close();
    }
    emf.close();
  }
}