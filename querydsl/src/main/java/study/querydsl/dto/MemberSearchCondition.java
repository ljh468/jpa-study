package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberSearchCondition {
  // 회원명, 팀명, 나이(ageGoe, ageLoe)

  private String username;

  private String teamName;

  private Integer ageGoe;

  private Integer ageLoe;

  // @QueryProjection
  // public MemberSearchCondition(String username, String teamName, Integer ageGoe, Integer ageLoe) {
  //   this.username = username;
  //   this.teamName = teamName;
  //   this.ageGoe = ageGoe;
  //   this.ageLoe = ageLoe;
  // }
}
