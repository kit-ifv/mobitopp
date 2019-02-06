package edu.kit.ifv.mobitopp.data.demand;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;

public class Demography implements Serializable {

  private static final long serialVersionUID = 1L;

  private final EmploymentDistribution employment;
  private final HouseholdDistribution household;
  private final Map<AttributeType, RangeDistributionIfc> rangeDistributions;

  public Demography(
      EmploymentDistribution employment, HouseholdDistribution household,
      Map<AttributeType, RangeDistributionIfc> rangeDistributions) {
    super();
    this.employment = employment;
    this.household = household;
    this.rangeDistributions = rangeDistributions;
  }

  public EmploymentDistribution employment() {
    return employment;
  }

  public HouseholdDistribution household() {
    return household;
  }

  public RangeDistributionIfc femaleAge() {
    return getDistribution(StandardAttribute.femaleAge);
  }

  public RangeDistributionIfc maleAge() {
    return getDistribution(StandardAttribute.maleAge);
  }

  public RangeDistributionIfc getDistribution(AttributeType type) {
    return rangeDistributions.get(type);
  }

  public RangeDistributionIfc income() {
    return getDistribution(StandardAttribute.income);
  }

  public void incrementHousehold(int type) {
    household.getItem(type).increment();
  }

  public void incrementEmployment(Employment employment) {
    this.employment.getItem(employment).increment();
  }

  public void incrementAge(Gender gender, int age) {
    if (Gender.MALE == gender) {
      maleAge().getItem(age).increment();
    } else {
      femaleAge().getItem(age).increment();
    }
  }

  public Demography createEmpty() {
    EmploymentDistribution emptyEmployment = employment.createEmpty();
    HouseholdDistribution emptyHousehold = household.createEmpty();
    Map<AttributeType, RangeDistributionIfc> emptyDistributions = emptyDistributions();
    return new Demography(emptyEmployment, emptyHousehold, emptyDistributions);
  }

  private Map<AttributeType, RangeDistributionIfc> emptyDistributions() {
    LinkedHashMap<AttributeType, RangeDistributionIfc> emptyDistributions = new LinkedHashMap<>();
    for (Entry<AttributeType, RangeDistributionIfc> entry : rangeDistributions
        .entrySet()) {
      emptyDistributions.put(entry.getKey(), entry.getValue().createEmpty());
    }
    return emptyDistributions;
  }

  @Override
  public int hashCode() {
    return Objects.hash(rangeDistributions, employment, household);
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
    return Objects.equals(rangeDistributions, other.rangeDistributions)
        && Objects.equals(employment, other.employment)
        && Objects.equals(household, other.household);
  }

  @Override
  public String toString() {
    return "Demography [employment=" + employment + ", household=" + household
        + ", continuousDistributions=" + rangeDistributions + "]";
  }

}
