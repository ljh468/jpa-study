package study.querydsl.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = {"username", "age"})
public class MemberDto {

  private String username;

  private int age;

  public MemberDto(String username, int age) {
    this.username = username;
    this.age = age;
  }
}
