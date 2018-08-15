package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
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
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.Example;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class DefaultOpportunityFormatTest {

	private static final ActivityType activityType = ActivityType.HOME;
	private static final int attractivity = 0;
	private static final int zoneOid = 1;
	
	private ZoneRepository zoneRepository;
	private DefaultOpportunityFormat format;
	private Zone zone;
	private Location location;
	private Opportunity original;

	@Before
	public void initialise() {
		zone = mock(Zone.class);
		location = Example.location;
		zoneRepository = mock(ZoneRepository.class);
		when(zone.getOid()).thenReturn(zoneOid);
		when(zoneRepository.getZoneByOid(zoneOid)).thenReturn(zone);
		original = new Opportunity(zone, activityType, location, attractivity);
		format = new DefaultOpportunityFormat(zoneRepository);
	}

	@Test
	public void prepare() {
		
		List<String> prepared = format.prepare(original);
		
		assertThat(prepared, is(equalTo(opportunity())));
	}
	
	@Test
	public void parse() {
		prepareExistingZone();
		
		Optional<Opportunity> parsed = format.parse(opportunity());
		
		Opportunity parsedValue = parsed.get();
		assertValue(Opportunity::zone, parsedValue, original);
		assertValue(Opportunity::activityType, parsedValue, original);
		assertValue(Opportunity::location, parsedValue, original);
		assertValue(Opportunity::attractivity, parsedValue, original);
	}
	
	private void prepareExistingZone() {
		when(zoneRepository.hasZone(zoneOid)).thenReturn(true);
	}
	
	@Test
	public void parseMissingZone() {
		prepareMissingZone();
		
		Optional<Opportunity> parsed = format.parse(opportunity());
		
		assertThat(parsed, isEmpty());
	}

	private void prepareMissingZone() {
		when(zoneRepository.hasZone(zoneOid)).thenReturn(false);
	}

	private List<String> opportunity() {
		return asList(
				valueOf(zoneOid),
				valueOf(activityType),
				Example.serialisedLocation,
				valueOf(attractivity)
				);
	}
}
