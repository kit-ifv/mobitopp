package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Arrays;
import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.demand.DefaultDistributions;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.IncomeDistribution;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;

public class ExampleDemandZones {

  private final DemandZone someZone;
  private final DemandZone otherZone;

  public ExampleDemandZones(DemandZone someZone, DemandZone otherZone) {
    super();
    this.someZone = someZone;
    this.otherZone = otherZone;
  }

  public static ExampleDemandZones create() {
    ExampleZones zones = ExampleZones.create();
    DemandZone someZone = new DemandZone(zones.someZone(), createDemography());
    DemandZone otherZone = new DemandZone(zones.otherZone(), createDemography());
    return new ExampleDemandZones(someZone, otherZone);
  }

  private static Demography createDemography() {
    DefaultDistributions defaultDistributions = new DefaultDistributions();
    EmploymentDistribution employment = EmploymentDistribution.createDefault();
    HouseholdDistribution household = HouseholdDistribution.createDefault();
    FemaleAgeDistribution femaleAge = defaultDistributions.createFemaleAge();
    MaleAgeDistribution maleAge = defaultDistributions.createMaleAge();
    IncomeDistribution income = defaultDistributions.createIncome();
    return new Demography(employment, household, femaleAge, maleAge, income);
  }

  public DemandZone someZone() {
    return someZone;
  }

  public DemandZone otherZone() {
    return otherZone;
  }

  public List<DemandZone> asList() {
    return Arrays.asList(someZone, otherZone);
  }

}
