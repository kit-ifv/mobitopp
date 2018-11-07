package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.IOException;

import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;
import edu.kit.ifv.mobitopp.simulation.Household;

public class BufferedDemandRepository implements DemandDataRepository {

	private final DemandDataSerialiser serialiser;
	private final Population population;
	private final OpportunityLocations locations;

	public BufferedDemandRepository(DemandDataSerialiser serialiser) {
		this.serialiser = serialiser;
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
	public void finishExecution() throws IOException {
		try {
			serialiser.serialise(population.households());
			serialiser.serialise(locations);
		} finally {
			serialiser.close();
		}
	}

}
