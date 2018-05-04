package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;
import edu.kit.ifv.mobitopp.simulation.Household;

public class LocalDemandDataRepository implements DemandDataRepository {

	private final Population population;
	private final OpportunityLocations locations;

	public LocalDemandDataRepository() {
		this.population = Population.empty();
		locations = new OpportunityLocations();
	}

	@Override
	public void store(DataForZone demandData) {
		for (Household household : demandData.getPopulationData().getHouseholds()) {
			population.add(household);
		}
		demandData.opportunities().forEach(locations::add);
	}

	@Override
	public void serialiseTo(DemandDataSerialiser serialiser) {
		serialiser.serialise(population);
		serialiser.serialise(locations);
	}

}
