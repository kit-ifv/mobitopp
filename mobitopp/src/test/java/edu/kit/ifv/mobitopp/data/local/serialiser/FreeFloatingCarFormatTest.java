package edu.kit.ifv.mobitopp.data.local.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.DefaultCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;

public class FreeFloatingCarFormatTest {

	private static final String company = "company";
	private static final int carId = 1;
	private static final int zoneOid = 2;
  private static final ZoneId zoneId = new ZoneId("2", zoneOid);

	private FreeFloatingCarSharingOrganization owner;
	private Car car;
	private CarSharingCar carsharingCar;
	private FreeFloatingCarFormat format;
	private Zone zone;
	private FreeFloatingCar freeFloatingCar;

	@Before
	public void initialise() {
		ZoneRepository zoneRepository = mock(ZoneRepository.class);
		owner = new FreeFloatingCarSharingOrganization(company);
		car = mock(Car.class);
		zone = mock(Zone.class);
		when(car.id()).thenReturn(carId);
		when(zone.getId()).thenReturn(zoneId);
		when(zoneRepository.getZoneByOid(zoneOid)).thenReturn(zone);
		carsharingCar = new DefaultCarSharingCar(car, owner);
		freeFloatingCar = new FreeFloatingCar(zone, carsharingCar);
		Collection<FreeFloatingCarSharingOrganization> owners = asList(owner);
		Collection<Car> cars = asList(car);
		format = new FreeFloatingCarFormat(zoneRepository, owners, cars);
	}

	@Test
	public void prepare() {
		List<String> prepared = format.prepare(freeFloatingCar);

		assertThat(prepared, contains(valueOf(zoneOid), company, valueOf(carId)));
	}

	@Test
	public void parse() {
		Optional<FreeFloatingCar> parsed = format.parse(format.prepare(freeFloatingCar));

		FreeFloatingCar freeFloatingCar = parsed.get();
		CarSharingCar parsedCar = freeFloatingCar.car;
		assertThat(freeFloatingCar.startZone, is(equalTo(zone)));
		assertValue(CarSharingCar::owner, parsedCar, carsharingCar);
		assertValue(CarSharingCar::id, parsedCar, carsharingCar);
		assertValue(CarSharingCar::position, parsedCar, carsharingCar);
		assertValue(CarSharingCar::carSegment, parsedCar, carsharingCar);
		assertValue(CarSharingCar::capacity, parsedCar, carsharingCar);
		assertValue(CarSharingCar::currentMileage, parsedCar, carsharingCar);
		assertValue(CarSharingCar::currentFuelLevel, parsedCar, carsharingCar);
		assertValue(CarSharingCar::maxRange, parsedCar, carsharingCar);

	}
}
