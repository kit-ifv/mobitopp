package edu.kit.ifv.mobitopp.data.local.configuration;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.matrix.ZonesToStops;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.ArrivalTimeFunction;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.FunctionEntry;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Profile;
import edu.kit.ifv.mobitopp.time.Time;

public class ZoneProfilesTest {

	private static final int first = 0;
	private static final int second = first + 1;
	private static final Time someDate = Data.someTime();
	private static final double margin = 1e-5d;
	private static final int expectedDuration = 1;
	private static final Time departure = someDate;
	private static final Time arrival = departure.plusMinutes(expectedDuration);

	private ZonesToStops zonesToStops;
	private Map<Zone, Profile> profiles;
	private Zone secondZone;
	private Stop someStop;

	@Before
	public void initialise() {
		zonesToStops = mock(ZonesToStops.class);
		profiles = new HashMap<>();
		secondZone = zone(second);
		someStop = Data.someStop();
		when(zonesToStops.stopFor(first)).thenReturn(someStop);
		profiles.put(secondZone, profile());
	}

	private Profile profile() {
		Profile profile = new Profile(null);
		profile.update(someStop, arrivalAt(arrival));
		return profile;
	}

	private ArrivalTimeFunction arrivalAt(Time time) {
		ArrivalTimeFunction arrivalTimeFunction = new ArrivalTimeFunction();
		Connection someConnection = Data.fromSomeToAnother();
		arrivalTimeFunction.update(new FunctionEntry(departure, time, someConnection));
		return arrivalTimeFunction;
	}

	private Zone zone(int id) {
		Zone zone = mock(Zone.class);
		when(zone.getOid()).thenReturn(id);
		return zone;
	}

	@Test
	public void travelTimeBetweenZones() {
		ZoneProfiles zoneProfiles = new ZoneProfiles(zonesToStops, profiles);

		double travelTime = zoneProfiles.getTravelTime(first, second, someDate);

		assertThat(travelTime, is(closeTo(expectedDuration, margin)));
	}
}
