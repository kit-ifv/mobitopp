package edu.kit.ifv.mobitopp.data.local.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneAreaType;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.local.serialiser.DefaultZoneFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.Example;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;

public class DefaultZoneFormatTest {

	private static final int oid = 1;
	private static final String id = "Z12345";
	private static final String name = "zone name";
	private static final ZoneAreaType areaType = ZoneAreaType.CITYOUTSKIRT;
	private static final ZoneClassificationType classification = ZoneClassificationType.areaOfInvestigation;
	private static final Location centroidLocation = Example.location;
	private DefaultZoneFormat format;
	private Zone zone;

	@Before
	public void initialise() {
		Attractivities attractivity = new Attractivities();
		ChargingDataForZone charging = mock(ChargingDataForZone.class);
		zone = new Zone(oid, id, name, areaType, classification, centroidLocation, attractivity,
				charging);
		ChargingDataResolver chargingData = mock(ChargingDataResolver.class);
		when(chargingData.chargingDataFor(oid)).thenReturn(charging);
		Map<Integer, Attractivities> attractivities = Collections.singletonMap(zoneId(), attractivity);
		format = new DefaultZoneFormat(chargingData, attractivities);
	}

	private int zoneId() {
		return Integer.parseInt(id.substring(1));
	}

	@Test
	public void prepare() {
		List<String> prepared = format.prepare(zone);
		
		assertThat(prepared, contains(valueOf(oid),
				id, 
				name, 
				valueOf(areaType),
				valueOf(classification),
				serialised(centroidLocation)));
	}

	private String serialised(Location location) {
		return new LocationParser().serialise(location);
	}
	
	@Test
	public void parse() {
		Optional<Zone> parsed = format.parse(format.prepare(zone));
		
		Zone parsedZone = parsed.get();
		assertValue(Zone::getOid, parsedZone, zone);
		assertValue(Zone::getId, parsedZone, zone);
		assertValue(Zone::getName, parsedZone, zone);
		assertValue(Zone::getAreaType, parsedZone, zone);
		assertValue(Zone::getClassification, parsedZone, zone);
		assertValue(Zone::centroidLocation, parsedZone, zone);
		assertValue(Zone::attractivities, parsedZone, zone);
	}
}
