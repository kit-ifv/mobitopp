package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
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
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DataForZone;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;

public class DefaultHouseholdFormatTest {

	private static final int zoneOid = 1;
	private static final ZoneId zoneId = new ZoneId("1", zoneOid);
	private static final int householdOid = 1;
	private HouseholdForSetup originalHousehold;
	
	private DefaultHouseholdFormat format;
	private ZoneRepository zoneRepository;
	private Zone zone;

	@BeforeClass
	public static void resetHouseholdIdSequence() throws ReflectiveOperationException {
		ReflectionHelper.resetHouseholdIdSequence();
	}

	@Before
	public void initialise() {
		DataForZone demandData = mock(DataForZone.class);
		zone = mock(Zone.class);
		originalHousehold = ExampleSetup.household(zone, ExampleSetup.firstHousehold);
		zoneRepository = mock(ZoneRepository.class);
		
		when(zone.getId()).thenReturn(zoneId);
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
				valueOf(ExampleSetup.householdYear),
				valueOf(ExampleSetup.householdNumber), 
				valueOf(ExampleSetup.nominalSize), 
				valueOf(ExampleSetup.domcode),
				valueOf(zoneOid),
				valueOf(ExampleSetup.serialisedLocation),
				valueOf(ExampleSetup.location.coordinatesP().getX()),
				valueOf(ExampleSetup.location.coordinatesP().getY()),
				valueOf(ExampleSetup.numberOfMinors),
				valueOf(ExampleSetup.numberOfNotSimulatedChildren),
				valueOf(ExampleSetup.noCars), 
        valueOf(ExampleSetup.income), 
        valueOf(ExampleSetup.incomeClass),
        valueOf(ExampleSetup.economicalStatus.getCode()),
				valueOf(ExampleSetup.canChargePrivately)
			);
	}
	@Test
	public void parsePopulation() throws IOException {
		when(zoneRepository.hasZone(anyInt())).thenReturn(true);
		List<String> serialisedHousehold = format.prepare(originalHousehold);
		
		Optional<HouseholdForSetup> household = format.parse(serialisedHousehold);

		assertValue(HouseholdForSetup::attributes, household.get(), originalHousehold);
	}
	
	@Test
	public void skipHousholdInMissingZone() {
		when(zoneRepository.hasZone(anyInt())).thenReturn(false);
		List<String> serialisedHousehold = format.prepare(originalHousehold);
		
		Optional<HouseholdForSetup> household = format.parse(serialisedHousehold);

		assertThat(household, isEmpty());
		verify(zoneRepository).hasZone(anyInt());
	}
}
