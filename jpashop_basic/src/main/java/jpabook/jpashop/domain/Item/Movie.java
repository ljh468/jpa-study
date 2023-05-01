package jpabook.jpashop.domain.Item;

import javax.persistence.Entity;

@Entity
// @DiscriminatorValue("M")
public class Movie extends Item{

  private String director;

  private String actor;

  public String getDirector() {
    return director;
  }

  public void setDirector(String director) {
    this.director = director;
  }

  public String getActor() {
    return actor;
  }

  public void setActor(String actor) {
    this.actor = actor;
  }
}
