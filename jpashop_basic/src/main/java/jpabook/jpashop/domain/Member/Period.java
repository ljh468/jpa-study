package jpabook.jpashop.domain.Member;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class Period {

  private LocalDateTime startDateTime;

  private LocalDateTime endDateTime;

  public Period() {
  }

  public Period(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  public boolean isWork(LocalDateTime currentDateTime) {
    return ((currentDateTime.isEqual(startDateTime) || currentDateTime.isAfter(startDateTime)) &&
            (currentDateTime.isEqual(endDateTime) || currentDateTime.isBefore(endDateTime)));
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public void setStartDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  public void setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }
}
