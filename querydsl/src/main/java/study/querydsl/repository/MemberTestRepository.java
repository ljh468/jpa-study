package study.querydsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.entity.Member;
import study.querydsl.repository.support.Querydsl5RepositorySupport;

import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@Repository
public class MemberTestRepository extends Querydsl5RepositorySupport {

  public MemberTestRepository() {
    super(Member.class);
  }

  public List<Member> basicSelect() {
    return select(member)
        .from(member)
        .fetch();
  }

  public List<Member> basicSelectFrom() {
    return selectFrom(member)
        .fetch();
  }

  public Page<Member> searchPageByApplyPage(MemberSearchCondition condition, Pageable pageable) {
    JPAQuery<Member> query = selectFrom(member)
        .leftJoin(member.team, team)
        .where(usernameEq(condition.getUsername()),
               teamNameEq(condition.getTeamName()),
               ageGoe(condition.getAgeGoe()),
               ageLoe(condition.getAgeLoe()));

    JPAQuery<Long> longCount = select(member.count())
        .from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(condition.getUsername()),
               teamNameEq(condition.getTeamName()),
               ageGoe(condition.getAgeGoe()),
               ageLoe(condition.getAgeLoe()));

    List<Member> content = getQuerydsl().applyPagination(pageable, query).fetch();
    return PageableExecutionUtils.getPage(content, pageable, longCount::fetchFirst);
  }

  /**
   * Simple version
   */
  public Page<Member> applyPagination(MemberSearchCondition condition, Pageable pageable) {
    return applyPagination(pageable, query -> query.selectFrom(member)
                                                   .leftJoin(member.team, team)
                                                   .where(usernameEq(condition.getUsername()),
                                                          teamNameEq(condition.getTeamName()),
                                                          ageGoe(condition.getAgeGoe()),
                                                          ageLoe(condition.getAgeLoe())));
  }

  public Page<Member> applyPagination2(MemberSearchCondition condition, Pageable pageable) {
    return applyPagination(pageable,
                           contentQuery -> contentQuery
                               .selectFrom(member)
                               .leftJoin(member.team, team)
                               .where(usernameEq(condition.getUsername()),
                                      teamNameEq(condition.getTeamName()),
                                      ageGoe(condition.getAgeGoe()),
                                      ageLoe(condition.getAgeLoe())),
                           countQuery -> countQuery
                               .select(member.count())
                               .from(member)
                               .leftJoin(member.team, team)
                               .where(usernameEq(condition.getUsername()),
                                      teamNameEq(condition.getTeamName()),
                                      ageGoe(condition.getAgeGoe()),
                                      ageLoe(condition.getAgeLoe()))
    );
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
