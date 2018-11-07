package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;

public class SerialisingDemandRepositoryTest {

	@Test
	public void serialsesZonesOnStorage() {
		DemandDataSerialiser serialiser = mock(DemandDataSerialiser.class);
		SerialisingDemandRepository repository = new SerialisingDemandRepository(serialiser);
		
		DataForZone demandData = ExampleDemandZones.create().someZone().getDemandData();
		OpportunityLocations expectedLocations = new OpportunityLocations();
		demandData.opportunities().forEach(expectedLocations::add);
		repository.store(demandData);
		
		verify(serialiser).serialise(demandData.getPopulationData().getHouseholds());
		verify(serialiser).serialise(expectedLocations);
	}
}
