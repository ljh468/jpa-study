package study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@Repository
public class MemberJpaRepository {

  private final EntityManager em;

  private final JPAQueryFactory queryFactory;

  @Autowired
  public MemberJpaRepository(EntityManager em) {
    this.em = em;
    this.queryFactory = new JPAQueryFactory(em);
  }

  public void save(Member member) {
    em.persist(member);
  }

  public Optional<Member> findById(Long id) {
    Member findMember = em.find(Member.class, id);
    return Optional.ofNullable(findMember);
  }

  public Optional<Member> findById_Querydsl(Long id) {
    Member findMember = queryFactory.selectFrom(member)
                                    .where(member.id.eq(id))
                                    .fetchOne();
    return Optional.ofNullable(findMember);
  }

  public List<Member> findAll() {
    return em.createQuery("select m from Member m", Member.class)
             .getResultList();
  }

  public List<Member> findAll_Querydsl() {
    return queryFactory.selectFrom(member)
                       .fetch();
  }

  public List<Member> findByUsername(String username) {
    return em.createQuery("select m from Member m "
                              + "where m.username = :username", Member.class)
             .setParameter("username", username)
             .getResultList();
  }

  public List<Member> findByUsername_Querydsl(String username) {
    return queryFactory.selectFrom(member)
                       .where(member.username.eq(username))
                       .fetch();
  }

  public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {
    BooleanBuilder builder = new BooleanBuilder();
    if (hasText(condition.getUsername())) {
      builder.and(member.username.eq(condition.getUsername()));
    }
    if (hasText(condition.getTeamName())) {
      builder.and(team.name.eq(condition.getTeamName()));
    }
    if (condition.getAgeGoe() != null) {
      builder.and(member.age.goe(condition.getAgeGoe()));
    }
    if (condition.getAgeLoe() != null) {
      builder.and(member.age.loe(condition.getAgeLoe()));
    }

    return queryFactory
        .select(new QMemberTeamDto(member.id.as("memberId"),
                                   member.username,
                                   member.age,
                                   team.id.as("teamId"),
                                   team.name.as("teamName")
        ))
        .from(member)
        .leftJoin(member.team, team)
        .where(builder)
        .fetch();
  }

  public List<MemberTeamDto> search(MemberSearchCondition condition) {
    return queryFactory
        .select(new QMemberTeamDto(member.id.as("memberId"),
                                   member.username,
                                   member.age,
                                   team.id.as("teamId"),
                                   team.name.as("teamName")
        ))
        .from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(condition.getUsername()),
               teamNameEq(condition.getTeamName()),
               ageLoe(condition.getAgeLoe()),
               ageGoe(condition.getAgeGoe()))
        .fetch();
  }

  private BooleanExpression usernameEq(String usernameCond) {
    return isEmpty(usernameCond) ? null : member.username.eq(usernameCond);
  }

  private BooleanExpression teamNameEq(String teamNameCond) {
    return isEmpty(teamNameCond) ? null : team.name.eq(teamNameCond);
  }

  private BooleanExpression ageGoe(Integer ageCond) {
    return ageCond != null ? member.age.goe(ageCond) : null;
  }

  private BooleanExpression ageLoe(Integer ageCond) {
    return ageCond != null ? member.age.loe(ageCond) : null;
  }
}
