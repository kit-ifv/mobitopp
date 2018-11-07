package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.HouseholdForDemand;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;

public class BufferedDemandRepositoryTest {

	private Zone zone;
	private DemandDataSerialiser serialiser;
	private HouseholdForDemand aHousehold;
	private HouseholdForDemand otherHousehold;
	private DataForZone demandData;
	private PopulationDataForZone populationData;
	private OpportunityDataForZone opportunityData;
	
	@BeforeClass
	public static void resetHouseholdIdSequence() throws ReflectiveOperationException {
		ReflectionHelper.resetHouseholdIdSequence();
	}

	@Before
	public void initialise() throws IOException {
		zone = mock(Zone.class);
		serialiser = mock(DemandDataSerialiser.class);
		aHousehold = Example.household(zone, Example.firstHousehold);
		otherHousehold = Example.household(zone, Example.secondHousehold);
		populationData = mock(PopulationDataForZone.class);
		opportunityData = mock(OpportunityDataForZone.class);
		
		demandData = createPopulation();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void storeHouseholds() throws IOException {
		DemandDataRepository repository = repository();

		repository.store(demandData);
		repository.finishExecution();

		verify(serialiser).serialise(any(Collection.class));
		verify(serialiser).serialise(any(OpportunityLocations.class));
		verify(demandData).getPopulationData();
		verify(populationData).getHouseholds();
		verify(demandData).opportunities();
		verify(opportunityData).forEach(any());
	}

	private BufferedDemandRepository repository() {
		return new BufferedDemandRepository(serialiser);
	}

	private DataForZone createPopulation() {
		DataForZone demandData = mock(DataForZone.class);
		List<Household> households = asList(aHousehold, otherHousehold);
		when(populationData.getHouseholds()).thenReturn(households);
		when(demandData.getPopulationData()).thenReturn(populationData);
		when(demandData.opportunities()).thenReturn(opportunityData);
		return demandData;
	}
}
