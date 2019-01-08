package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.IncomeDistribution;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;

public class DemographyBuilder {

  private static final String malePrefix = "age:m:";
  private static final String femalePrefix = "age:f:";
  private static final String incomePrefix = "income:";

  private final StructuralData structuralData;

  public DemographyBuilder(StructuralData structuralData) {
    super();
    this.structuralData = structuralData;
  }

  public Demography build() {
    HouseholdDistribution household = parseHouseholdDistribution();
    MaleAgeDistribution maleAge = parseMaleDistribution();
    FemaleAgeDistribution femaleAge = parseFemaleDistribution();
    EmploymentDistribution employment = parseJobDistribution();
    IncomeDistribution income = parseIncomeDistribution();
    return new Demography(employment, household, femaleAge, maleAge, income);
  }

  private FemaleAgeDistribution parseFemaleDistribution() {
    return new ContinuousDistributionBuilder(structuralData, femalePrefix)
        .buildFor(FemaleAgeDistribution::new);
  }

  private MaleAgeDistribution parseMaleDistribution() {
    return new ContinuousDistributionBuilder(structuralData, malePrefix)
        .buildFor(MaleAgeDistribution::new);
  }

  private HouseholdDistribution parseHouseholdDistribution() {
    return new HouseholdDistributionBuilder(structuralData).build();
  }

  private EmploymentDistribution parseJobDistribution() {
    return new EmploymentDistributionBuilder(structuralData).build();
  }

  private IncomeDistribution parseIncomeDistribution() {
    return new ContinuousDistributionBuilder(structuralData, incomePrefix)
        .buildFor(IncomeDistribution::new);
  }
}
