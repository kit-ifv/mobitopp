package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.IncomeDistribution;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;
import edu.kit.ifv.mobitopp.populationsynthesis.DataForZone;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;

public class DemandZone {

  private final Zone zone;
  private final Demography nominalDemography;
  private final Demography actualDemography;
  private final PopulationForSetup population;

  public DemandZone(Zone zone, Demography nominalDemand) {
    super();
    this.zone = zone;
    this.nominalDemography = nominalDemand;
    actualDemography = emptyDemographyFrom(nominalDemand);
    this.population = new PopulationForSetup();
  }

  private Demography emptyDemographyFrom(Demography nominalDemand) {
    EmploymentDistribution employment = EmploymentDistribution.createDefault();
    HouseholdDistribution household = householdDistribution(nominalDemand);
    FemaleAgeDistribution femaleAge = femaleAgeDistribution(nominalDemand);
    MaleAgeDistribution maleAge = maleAgeDistribution(nominalDemand);
    IncomeDistribution income = incomeDistribution(nominalDemand);
    return new Demography(employment, household, femaleAge, maleAge, income);
  }

  private FemaleAgeDistribution femaleAgeDistribution(Demography demandModel) {
    FemaleAgeDistribution distribution = new FemaleAgeDistribution();
    demandModel
        .femaleAge()
        .items()
        .map(ContinuousDistributionItem::createEmpty)
        .forEach(distribution::addItem);
    return distribution;
  }

  private MaleAgeDistribution maleAgeDistribution(Demography demandModel) {
    MaleAgeDistribution distribution = new MaleAgeDistribution();
    demandModel
        .maleAge()
        .items()
        .map(ContinuousDistributionItem::createEmpty)
        .forEach(distribution::addItem);
    return distribution;
  }

  private HouseholdDistribution householdDistribution(Demography demandModel) {
    HouseholdDistribution distribution = new HouseholdDistribution();
    demandModel
        .household()
        .items()
        .map(HouseholdDistributionItem::createEmpty)
        .forEach(distribution::addItem);
    return distribution;
  }
  
  private IncomeDistribution incomeDistribution(Demography demandModel) {
    IncomeDistribution distribution = new IncomeDistribution();
    demandModel
        .income()
        .items()
        .map(ContinuousDistributionItem::createEmpty)
        .forEach(distribution::addItem);
    return distribution;
  }

  public int getOid() {
    return zone.getOid();
  }

  public String getId() {
    return zone.getId();
  }

  public Zone zone() {
    return zone;
  }

  public PopulationForSetup getPopulation() {
    return population;
  }

  public OpportunityDataForZone opportunities() {
    return zone.getDemandData().opportunities();
  }

  public DataForZone getDemandData() {
    return zone.getDemandData();
  }

  public Demography nominalDemography() {
    return nominalDemography;
  }

  public Demography actualDemography() {
    return actualDemography;
  }

  public AreaType getAreaType() {
    return zone.getAreaType();
  }

}
