package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.IncomeDistribution;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;
import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;

public class DemographyBuilder {

  private static final String malePrefix = "age:m:";
  private static final String femalePrefix = "age:f:";
  private static final String incomePrefix = "income:";

  private final DemographyData demographyData;

  public DemographyBuilder(DemographyData demographyData) {
    super();
    this.demographyData = demographyData;
  }

  public Demography build(String zoneId) {
    HouseholdDistribution household = parseHouseholdDistribution(zoneId);
    MaleAgeDistribution maleAge = parseMaleDistribution(zoneId);
    FemaleAgeDistribution femaleAge = parseFemaleDistribution(zoneId);
    EmploymentDistribution employment = parseJobDistribution(zoneId);
    IncomeDistribution income = parseIncomeDistribution(zoneId);
    return new Demography(employment, household, femaleAge, maleAge, income);
  }

  private FemaleAgeDistribution parseFemaleDistribution(String zoneId) {
    StructuralData structuralData = demographyData.get(femalePrefix);
    return new ContinuousDistributionBuilder(structuralData, femalePrefix)
        .buildFor(zoneId, FemaleAgeDistribution::new);
  }

  private MaleAgeDistribution parseMaleDistribution(String zoneId) {
    StructuralData structuralData = demographyData.get(malePrefix);
    return new ContinuousDistributionBuilder(structuralData, malePrefix)
        .buildFor(zoneId, MaleAgeDistribution::new);
  }

  private HouseholdDistribution parseHouseholdDistribution(String zoneId) {
    StructuralData structuralData = demographyData.get("household_size:");
    return new HouseholdDistributionBuilder(structuralData).build(zoneId);
  }

  private EmploymentDistribution parseJobDistribution(String zoneId) {
    StructuralData structuralData = demographyData.get("employment:");
    return new EmploymentDistributionBuilder(structuralData).build(zoneId);
  }

  private IncomeDistribution parseIncomeDistribution(String zoneId) {
    StructuralData structuralData = demographyData.get(incomePrefix);
    return new ContinuousDistributionBuilder(structuralData, incomePrefix)
        .buildFor(zoneId, IncomeDistribution::new);
  }
}
