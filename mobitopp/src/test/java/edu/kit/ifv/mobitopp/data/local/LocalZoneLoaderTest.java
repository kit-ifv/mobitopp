package edu.kit.ifv.mobitopp.data.local;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.dataimport.ZonesReader;

public class LocalZoneLoaderTest {

	private static final ZoneId zoneId = new ZoneId("11", 1);
	private static final ZoneId anotherId = new ZoneId("22", 2);
	private static final int limitedSize = 1;
	
	private ZonesReader reader;
	private Zone zone;
	private Zone anotherZone;
	private Zone differentZone;
	private LocalZoneLoader loader;

	@Before
	public void initialise() {
		reader = mock(ZonesReader.class);
		zone = mock(Zone.class);
		when(zone.getId()).thenReturn(zoneId);
		anotherZone = mock(Zone.class);
		when(anotherZone.getId()).thenReturn(zoneId);
		differentZone = mock(Zone.class);
		when(differentZone.getId()).thenReturn(anotherId);
		loader = new LocalZoneLoader(reader);
	}
	
	@Test
	public void mapsAllZones() {
		when(reader.getZones()).thenReturn(asList(zone, differentZone));
		
		Map<ZoneId, Zone> mapping = loader.mapAllZones();
		
		assertThat(mapping.entrySet(), hasSize(2));
		assertThat(mapping.get(zoneId), is(equalTo(zone)));
		assertThat(mapping.get(anotherId), is(equalTo(differentZone)));
		verify(reader).getZones();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failsOnADuplicatedZoneOid() {
		when(reader.getZones()).thenReturn(asList(zone, anotherZone));
		
		loader.mapAllZones();
	}
	
	@Test
	public void restrictsNumberOfLoadedZones() {
		when(reader.getZones()).thenReturn(asList(zone, differentZone));
		
		Map<ZoneId, Zone> zones = loader.mapZones(limitedSize);
		
		assertThat(zones.entrySet(), hasSize(limitedSize));
	}
	
}
