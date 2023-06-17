package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public MemberRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
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

  // fetchResults deprecate
  @Override
  public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
    QueryResults<MemberTeamDto> results = queryFactory
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
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetchResults();

    List<MemberTeamDto> content = results.getResults();
    long total = results.getTotal();
    return new PageImpl<>(content, pageable, total);
  }

  // countQuery deprecate
  @Override
  public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
    List<MemberTeamDto> content = queryFactory.select(new QMemberTeamDto(
                                                  member.id,
                                                  member.username,
                                                  member.age,
                                                  team.id,
                                                  team.name
                                              ))
                                              .from(member)
                                              .leftJoin(member.team, team)
                                              .where(usernameEq(condition.getUsername()),
                                                     teamNameEq(condition.getTeamName()),
                                                     ageGoe(condition.getAgeGoe()),
                                                     ageLoe(condition.getAgeLoe()))
                                              .offset(pageable.getOffset())
                                              .limit(pageable.getPageSize())
                                              .fetch();

    // long total = queryFactory.select(Wildcard.count)
    //                          // .select(member.count())
    //                          .from(member)
    //                          .leftJoin(member.team, team)
    //                          .where(usernameEq(condition.getUsername()),
    //                                 teamNameEq(condition.getTeamName()),
    //                                 ageGoe(condition.getAgeGoe()),
    //                                 ageLoe(condition.getAgeLoe()))
    //                          .fetchFirst();

    JPAQuery<Long> longCount = queryFactory.select(member.count())
                                           .from(member)
                                           .leftJoin(member.team, team)
                                           .where(usernameEq(condition.getUsername()),
                                                  teamNameEq(condition.getTeamName()),
                                                  ageGoe(condition.getAgeGoe()),
                                                  ageLoe(condition.getAgeLoe()));

    // return new PageImpl<>(content, pageable, total);
    return PageableExecutionUtils.getPage(content, pageable, longCount::fetchFirst);
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
