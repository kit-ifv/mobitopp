package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;
import edu.kit.ifv.mobitopp.simulation.Household;

public class SerialisingDemandRepositoryTest {

	@Test
	public void serialsesZonesOnStorage() {
		DemandDataSerialiser serialiser = mock(DemandDataSerialiser.class);
		SerialisingDemandRepository repository = new SerialisingDemandRepository(serialiser);
		HouseholdForSetup setupHousehold = mock(HouseholdForSetup.class);
		Household household = mock(Household.class);
		when(setupHousehold.toHousehold()).thenReturn(household);

		DemandZone demandZone = ExampleDemandZones.create().getSomeZone();
		demandZone.getPopulation().addHousehold(setupHousehold);
		OpportunityLocations expectedLocations = new OpportunityLocations();
		demandZone.opportunities().forEach(expectedLocations::add);

		repository.store(demandZone);

		demandZone.getPopulation().households().forEach(verify(serialiser)::serialise);
		verify(serialiser).serialise(expectedLocations);
	}
}
