package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.IOException;

import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;

public class SerialisingDemandRepository implements DemandDataRepository {

	private final DemandDataSerialiser serialiser;

	public SerialisingDemandRepository(DemandDataSerialiser serialiser) {
		this.serialiser = serialiser;
	}

	@Override
	public void store(DataForZone demandData) {
		serialiser.serialise(demandData.getPopulationData().getHouseholds());
		OpportunityLocations locations = new OpportunityLocations();
		demandData.opportunities().forEach(locations::add);
		serialiser.serialise(locations);
	}

	@Override
	public void finishExecution() throws IOException {
		serialiser.close();
	}
}
