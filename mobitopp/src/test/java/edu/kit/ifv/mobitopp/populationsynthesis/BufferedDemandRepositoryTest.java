package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PopulationForSetup;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;

public class BufferedDemandRepositoryTest {

	private Zone zone;
	private DemandDataSerialiser serialiser;
	private HouseholdForSetup aHousehold;
	private HouseholdForSetup otherHousehold;
	private DemandZone demandData;
	private PopulationForSetup population;
	private OpportunityDataForZone opportunityData;
	
	@BeforeClass
	public static void resetHouseholdIdSequence() throws ReflectiveOperationException {
		ReflectionHelper.resetHouseholdIdSequence();
	}

	@Before
	public void initialise() throws IOException {
		zone = mock(Zone.class);
		serialiser = mock(DemandDataSerialiser.class);
		aHousehold = ExampleSetup.household(zone, ExampleSetup.firstHousehold);
		otherHousehold = ExampleSetup.household(zone, ExampleSetup.secondHousehold);
		population = mock(PopulationForSetup.class);
		opportunityData = mock(OpportunityDataForZone.class);
		
		demandData = createPopulation();
	}

	@Test
	public void storeHouseholds() throws IOException {
		DemandDataRepository repository = repository();

		repository.store(demandData);
		repository.finishExecution();

		verify(serialiser, times(2)).serialise(any(Household.class));
		verify(serialiser).serialise(any(OpportunityLocations.class));
		verify(demandData).getPopulation();
		verify(population).households();
		verify(demandData).opportunities();
		verify(opportunityData).forEach(any());
	}

	private BufferedDemandRepository repository() {
		return new BufferedDemandRepository(serialiser);
	}

	private DemandZone createPopulation() {
		DemandZone demandZone = mock(DemandZone.class);
		Stream<HouseholdForSetup> households = Stream.of(aHousehold, otherHousehold);
		when(population.households()).thenReturn(households);
		when(demandZone.getPopulation()).thenReturn(population);
		when(demandZone.opportunities()).thenReturn(opportunityData);
		return demandZone;
	}
}
