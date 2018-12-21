package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.person.PersonId;

public class PersonAttributes {

  public final int oid;
  public final PersonId id;
  public final Household household;
  public final int age;
  public final Employment employment;
  public final Gender gender;
  public final int income;
  public final boolean hasBike;
  public final boolean hasAccessToCar;
  public final boolean hasPersonalCar;
  public final boolean hasCommuterTicket;
  public final boolean hasLicense;

  public PersonAttributes(
      PersonId id, Household household, int age, Employment employment, Gender gender, int income,
      boolean hasBike, boolean hasAccessToCar, boolean hasPersonalCar, boolean hasCommuterTicket,
      boolean hasLicense) {
    super();
    this.oid = id.getOid();
    this.id = id;
    this.household = household;
    this.age = age;
    this.employment = employment;
    this.gender = gender;
    this.income = income;
    this.hasBike = hasBike;
    this.hasAccessToCar = hasAccessToCar;
    this.hasPersonalCar = hasPersonalCar;
    this.hasCommuterTicket = hasCommuterTicket;
    this.hasLicense = hasLicense;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + age;
    result = prime * result + ((employment == null) ? 0 : employment.hashCode());
    result = prime * result + ((gender == null) ? 0 : gender.hashCode());
    result = prime * result + (hasAccessToCar ? 1231 : 1237);
    result = prime * result + (hasBike ? 1231 : 1237);
    result = prime * result + (hasCommuterTicket ? 1231 : 1237);
    result = prime * result + (hasLicense ? 1231 : 1237);
    result = prime * result + (hasPersonalCar ? 1231 : 1237);
    result = prime * result + ((household == null) ? 0 : household.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + income;
    result = prime * result + oid;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PersonAttributes other = (PersonAttributes) obj;
    if (age != other.age)
      return false;
    if (employment != other.employment)
      return false;
    if (gender != other.gender)
      return false;
    if (hasAccessToCar != other.hasAccessToCar)
      return false;
    if (hasBike != other.hasBike)
      return false;
    if (hasCommuterTicket != other.hasCommuterTicket)
      return false;
    if (hasPersonalCar != other.hasPersonalCar)
      return false;
    if (household == null) {
      if (other.household != null)
        return false;
    } else if (!household.equals(other.household))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (income != other.income)
      return false;
    if (oid != other.oid)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "PersonAttributes [oid=" + oid + ", id=" + id + ", household=" + household + ", age="
        + age + ", employment=" + employment + ", gender=" + gender + ", income=" + income
        + ", hasBike=" + hasBike + ", hasAccessToCar=" + hasAccessToCar + ", hasPersonalCar="
        + hasPersonalCar + ", hasCommuterTicket=" + hasCommuterTicket + "]";
  }

}
