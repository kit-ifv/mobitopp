package edu.kit.ifv.mobitopp.populationsynthesis;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonFixedDestination;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;

public class DefaultFixedDestinationFormatTest {

	private static final Location location = ExampleSetup.location;
	private static final ActivityType activityType = ActivityType.HOME;
	private static final int zoneOid = 1;
	private static final ZoneId zoneId = new ZoneId("1", zoneOid);
	private static final int personOid = 2;
	private static final int personNumber = 1;
	private static final int householdOid = 1;
	private static final short householdYear = 2000;
	private static final int householdNumber = 2;

	private ZoneRepository zoneRepository;
	private FixedDestination fixedDestination;
	private Zone zone;
	private PersonFixedDestination personDestination;
	private PersonId person;
	private DefaultFixedDestinationFormat format;

	@Before
	public void initialise() {
		zoneRepository = mock(ZoneRepository.class);
		zone = mock(Zone.class);
		HouseholdId householdId = new HouseholdId(householdOid, householdYear, householdNumber);
    person = new PersonId(personOid, householdId, personNumber);
		when(zone.getId()).thenReturn(zoneId);
		fixedDestination = new FixedDestination(activityType, zone, location);
		personDestination = new PersonFixedDestination(person , fixedDestination);
		format = new DefaultFixedDestinationFormat(zoneRepository);
	}

	@Test
	public void prepareFixedDestination() {
		List<String> prepared = format.prepare(personDestination);

		assertThat(prepared, is(equalTo(fixedDestination())));
	}
	
	@Test
	public void parse() {
		prepareExistingZone();
		
		Optional<PersonFixedDestination> parsed = format.parse(fixedDestination());
		
		assertThat(parsed, hasValue(personDestination));
	}
	
	private void prepareExistingZone() {
		when(zoneRepository.hasZone(zoneOid)).thenReturn(true);
		when(zoneRepository.getZoneByOid(zoneOid)).thenReturn(zone);
	}
	
	@Test
	public void parseMissingZone() {
		prepareMissingZone();
		
		Optional<PersonFixedDestination> parsed = format.parse(fixedDestination());
		
		assertThat(parsed, isEmpty());
	}

	private void prepareMissingZone() {
		when(zoneRepository.hasZone(zoneOid)).thenReturn(false);
	}

	private List<String> fixedDestination() {
		String serialisedLocation = new LocationParser().serialise(location);
		return asList(
				valueOf(personOid),
				valueOf(personNumber),
				valueOf(householdOid),
				valueOf(householdYear),
				valueOf(householdNumber),
				valueOf(activityType),
				valueOf(zoneOid),
				valueOf(serialisedLocation)
		);
	}
}
