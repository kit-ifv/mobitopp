package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistribution;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;

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
    DemandZone someZone = new DemandZone(zones.someZone(), someDemography());
    DemandZone otherZone = new DemandZone(zones.otherZone(), otherDemography());
    return new ExampleDemandZones(someZone, otherZone);
  }

  private static Demography someDemography() {
    EmploymentDistribution employment = EmploymentDistribution.createDefault();
    HouseholdDistribution household = someHouseholdDistribution();
    ContinuousDistributionIfc femaleAge = someFemaleDistribution();
    ContinuousDistributionIfc maleAge = someMaleDistribution();
    ContinuousDistributionIfc income = someIncomeDistribution();
    Map<AttributeType, ContinuousDistributionIfc> continuousDistributions = new LinkedHashMap<>();
    continuousDistributions.put(StandardAttribute.maleAge, maleAge);
    continuousDistributions.put(StandardAttribute.femaleAge, femaleAge);
    continuousDistributions.put(StandardAttribute.income, income);
    return new Demography(employment, household, continuousDistributions);
  }

  private static ContinuousDistributionIfc someMaleDistribution() {
    ContinuousDistributionIfc distribution = new ContinuousDistribution();
    distribution.addItem(new ContinuousDistributionItem(0, 10, 4));
    distribution.addItem(new ContinuousDistributionItem(11, Integer.MAX_VALUE, 2));
    return distribution;
  }

  private static ContinuousDistributionIfc someFemaleDistribution() {
    ContinuousDistributionIfc distribution = new ContinuousDistribution();
    distribution.addItem(new ContinuousDistributionItem(0, 5, 2));
    distribution.addItem(new ContinuousDistributionItem(6, Integer.MAX_VALUE, 1));
    return distribution;
  }

  private static HouseholdDistribution someHouseholdDistribution() {
    HouseholdDistribution distribution = new HouseholdDistribution();
    distribution.addItem(new HouseholdDistributionItem(1, 2));
    distribution.addItem(new HouseholdDistributionItem(2, 3));
    return distribution;
  }

  private static Demography otherDemography() {
    EmploymentDistribution employment = EmploymentDistribution.createDefault();
    HouseholdDistribution household = someHouseholdDistribution();
    ContinuousDistributionIfc femaleAge = someFemaleDistribution();
    ContinuousDistributionIfc maleAge = someMaleDistribution();
    ContinuousDistributionIfc income = someIncomeDistribution();
    Map<AttributeType, ContinuousDistributionIfc> continuousDistributions = new LinkedHashMap<>();
    continuousDistributions.put(StandardAttribute.maleAge, maleAge);
    continuousDistributions.put(StandardAttribute.femaleAge, femaleAge);
    continuousDistributions.put(StandardAttribute.income, income);
    return new Demography(employment, household, continuousDistributions);
  }

  private static ContinuousDistributionIfc someIncomeDistribution() {
    ContinuousDistributionIfc income = new ContinuousDistribution();
    income.addItem(new ContinuousDistributionItem(0, 1000, 1));
    income.addItem(new ContinuousDistributionItem(1001, 2000, 2));
    income.addItem(new ContinuousDistributionItem(2001, Integer.MAX_VALUE, 2));
    return income;
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
