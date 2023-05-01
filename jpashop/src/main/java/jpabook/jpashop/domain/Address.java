package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
public class Address {

  @Column(length = 10)
  private String city;

  @Column(length = 20)
  private String street;

  @Column(length = 5)
  private String zipcode;

  public String fullAddress() {
    return getCity() + " " + getStreet() + " " + getZipcode();
  }

  public String isValid() {
    return null;
  }

  protected Address() {
  }

  public Address(String city, String street, String zipcode) {
    this.city = city;
    this.street = street;
    this.zipcode = zipcode;
  }

  private void setCity(String city) {
    this.city = city;
  }

  private void setStreet(String street) {
    this.street = street;
  }

  private void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {return true;}
    if (o == null || getClass() != o.getClass()) {return false;}
    Address address = (Address) o;
    return Objects.equals(getCity(), address.getCity()) &&
        Objects.equals(getStreet(), address.getStreet()) &&
        Objects.equals(getZipcode(), address.getZipcode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCity(), getStreet(), getZipcode());
  }
}
