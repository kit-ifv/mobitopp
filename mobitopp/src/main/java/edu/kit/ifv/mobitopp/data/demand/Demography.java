package edu.kit.ifv.mobitopp.data.demand;

import java.io.Serializable;
import java.util.Objects;

import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;

public class Demography implements Serializable {

  private static final long serialVersionUID = 1L;

  private final EmploymentDistribution employment;
  private final HouseholdDistribution household;
  private final FemaleAgeDistribution femaleAge;
  private final MaleAgeDistribution maleAge;
  private final IncomeDistribution income;

  public Demography(
      EmploymentDistribution employment, HouseholdDistribution household,
      FemaleAgeDistribution femaleAge, MaleAgeDistribution maleAge, IncomeDistribution income) {
    super();
    this.employment = employment;
    this.household = household;
    this.femaleAge = femaleAge;
    this.maleAge = maleAge;
    this.income = income;
  }

  public EmploymentDistribution employment() {
    return employment;
  }

  public HouseholdDistribution household() {
    return household;
  }

  public FemaleAgeDistribution femaleAge() {
    return femaleAge;
  }

  public MaleAgeDistribution maleAge() {
    return maleAge;
  }

  public IncomeDistribution income() {
    return income;
  }

  public void incrementHousehold(int type) {
    household.getItem(type).increment();
  }

  public void incrementEmployment(Employment employment) {
    this.employment.getItem(employment).increment();
  }

  public void incrementAge(Gender gender, int age) {
    if (Gender.MALE == gender) {
      maleAge.getItem(age).increment();
    } else {
      femaleAge.getItem(age).increment();
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(employment, femaleAge, household, income, maleAge);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Demography other = (Demography) obj;
    return Objects.equals(employment, other.employment)
        && Objects.equals(femaleAge, other.femaleAge) && Objects.equals(household, other.household)
        && Objects.equals(income, other.income) && Objects.equals(maleAge, other.maleAge);
  }

  @Override
  public String toString() {
    return "Demography [employment=" + employment + ", household=" + household + ", femaleAge="
        + femaleAge + ", maleAge=" + maleAge + ", income=" + income + "]";
  }

}
