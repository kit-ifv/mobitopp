package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DataForZone;
import edu.kit.ifv.mobitopp.populationsynthesis.Example;
import edu.kit.ifv.mobitopp.populationsynthesis.PopulationDataForZone;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;

public class DefaultHouseholdFormatTest {

	private static final int zoneOid = 1;
	private static final int householdOid = 1;
	private Household originalHousehold;
	
	private DefaultHouseholdFormat format;
	private ZoneRepository zoneRepository;
	private Zone zone;
	private PopulationDataForZone populationData;

	@BeforeClass
	public static void resetHouseholdIdSequence() throws ReflectiveOperationException {
		ReflectionHelper.resetHouseholdIdSequence();
	}

	@Before
	public void initialise() {
		populationData = mock(PopulationDataForZone.class);
		DataForZone demandData = mock(DataForZone.class);
		zone = mock(Zone.class);
		originalHousehold = Example.household(zone, Example.firstHousehold);
		zoneRepository = mock(ZoneRepository.class);
		
		when(demandData.getPopulationData()).thenReturn(populationData);
		when(zone.getOid()).thenReturn(zoneOid);
		when(zone.getDemandData()).thenReturn(demandData);
		when(zoneRepository.getZoneByOid(anyInt())).thenReturn(zone);
		
		format = new DefaultHouseholdFormat(zoneRepository);
	}

	@Test
	public void serialiseHouseholdAttributes() throws IOException {
		List<String> prepared = format.prepare(originalHousehold);

		assertThat(prepared, is(equalTo(householdOf(householdOid))));
	}

	private List<String> householdOf(int householdOid) throws IOException {
		return asList(
				valueOf(householdOid), 
				valueOf(Example.householdYear),
				valueOf(Example.householdNumber), 
				valueOf(Example.nominalSize), 
				valueOf(Example.domcode),
				valueOf(zoneOid), 
				valueOf(Example.serialisedLocation), 
				valueOf(Example.numberOfNotSimulatedChildren),
				valueOf(Example.totalNumberOfCars), 
				valueOf(Example.income),
				valueOf(Example.canChargePrivately)
			);
	}
	@Test
	public void parsePopulation() throws IOException {
		List<String> serialisedHousehold = format.prepare(originalHousehold);
		
		Household household = format.parse(serialisedHousehold);

		assertValue(Household::attributes, household, originalHousehold);
		verify(populationData).addHousehold(household);
	}
}
