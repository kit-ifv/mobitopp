package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.demand.Demography;
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
    actualDemography = nominalDemand.createEmpty();
    this.population = new PopulationForSetup();
  }

  public ZoneId getId() {
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
