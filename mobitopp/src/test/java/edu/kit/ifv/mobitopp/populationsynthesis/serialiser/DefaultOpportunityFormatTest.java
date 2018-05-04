package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

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
		Opportunity parsed = format.parse(opportunity());
		
		assertValue(Opportunity::zone, parsed, original);
		assertValue(Opportunity::activityType, parsed, original);
		assertValue(Opportunity::location, parsed, original);
		assertValue(Opportunity::attractivity, parsed, original);
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
