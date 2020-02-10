package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.DataForZone;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;

public class DemandZone {

  private final Zone zone;
  private final Demography nominalDemography;
  private final boolean generatePopulation;
  private final Demography actualDemography;
  private final PopulationForSetup population;

  public DemandZone(final Zone zone, final Demography nominalDemand, final boolean generatePopulation) {
    super();
    this.zone = zone;
    this.nominalDemography = nominalDemand;
		this.generatePopulation = generatePopulation;
    actualDemography = nominalDemand.createEmpty();
    this.population = new PopulationForSetup();
  }
  
  public DemandZone(final Zone zone, final Demography nominalDemand) {
    this(zone, nominalDemand, true);
  }

  public ZoneId getId() {
    return zone.getId();
  }

  public Zone zone() {
    return zone;
  }
  
  public boolean shouldGeneratePopulation() {
  	return generatePopulation;
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

	@Override
	public String toString() {
		return "DemandZone [zone=" + zone + "]";
	}

}
