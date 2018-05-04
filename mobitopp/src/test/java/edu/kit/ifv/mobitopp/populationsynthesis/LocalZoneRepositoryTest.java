package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;

public class LocalZoneRepositoryTest {

	private static final int zoneOid = 1;
	private static final int missingOid = 2;
	private Zone zone;
	private LocalZoneRepository repository;

	@Before
	public void initialise() {
		zone = mock(Zone.class);
		Map<Integer, Zone> zones = Collections.singletonMap(zoneOid, zone);
		repository = new LocalZoneRepository(zones);
	}

	@Test
	public void findsAZone() {
		Zone zoneByOid = repository.getZoneByOid(zoneOid);

		assertThat(zoneByOid, is(sameInstance(zone)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsForMissingZone() {
		repository.getZoneByOid(missingOid);
	}

}
