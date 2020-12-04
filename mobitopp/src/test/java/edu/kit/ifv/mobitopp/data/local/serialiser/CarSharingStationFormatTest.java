package edu.kit.ifv.mobitopp.data.local.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class CarSharingStationFormatTest {

  private static final String company = "My Company";
  private static final int zoneOid = 1;
	private static final ZoneId zoneId = new ZoneId("1", zoneOid);
	private static final Integer id = 1;
	private static final String name = "station";
	private static final String parkingSpace = "parkingSpace";
	private static final Location location = ExampleSetup.location;
	private static final Integer numberOfCars = 2;
	
	private StationBasedCarSharingOrganization organization;
	private Zone zone;
	private CarSharingStation station;
	private CarSharingStationFormat format;
	
	@Before
	public void initialise() {
		organization = new StationBasedCarSharingOrganization(company);
		zone = mock(Zone.class);
		when(zone.getId()).thenReturn(zoneId);
		station = new CarSharingStation(organization, zone, id, name, parkingSpace, location, numberOfCars);
		List<StationBasedCarSharingOrganization> companies = asList(organization);
		ZoneRepository zoneRepository = mock(ZoneRepository.class);
		when(zoneRepository.getZoneByOid(zoneOid)).thenReturn(zone);
		
		format = new CarSharingStationFormat(companies, zoneRepository);
	}

	@Test
	public void prepare() {
		List<String> prepared = format.prepare(station);
		
		assertThat(prepared, contains(valueOf(company),
				valueOf(zoneOid),
				valueOf(id),
				valueOf(name),
				valueOf(parkingSpace),
				serialised(location),
				valueOf(numberOfCars)));
	}

	private String serialised(Location location) {
		return new LocationParser().serialise(location);
	}
	
	@Test
	public void parse() {
		Optional<CarSharingStation> parsed = format.parse(format.prepare(station));
		
		CarSharingStation actual = parsed.get();
		assertValue(s -> s.carSharingCompany, actual, station);
		assertValue(s -> s.zone, actual, station);
		assertValue(s -> s.id, actual, station);
		assertValue(s -> s.name, actual, station);
		assertValue(s -> s.parkingSpace, actual, station);
		assertValue(s -> s.location, actual, station);
		assertValue(s -> s.numberOfCars, actual, station);
	}
}
