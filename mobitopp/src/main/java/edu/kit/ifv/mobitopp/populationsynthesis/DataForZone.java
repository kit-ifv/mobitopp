package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;

public class DataForZone
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final PopulationDataForZone population; 
	private final OpportunityDataForZone opportunities;

	public DataForZone(Zone zone, Attractivities attractivities) {
		super();
		this.population = new PopulationDataForZone();
		this.opportunities = new OpportunityDataForZone(zone.getId(), attractivities);
  }

	public PopulationDataForZone getPopulationData() {
		return this.population;
	}

  public OpportunityDataForZone opportunities() {
		return this.opportunities;
	}

}
