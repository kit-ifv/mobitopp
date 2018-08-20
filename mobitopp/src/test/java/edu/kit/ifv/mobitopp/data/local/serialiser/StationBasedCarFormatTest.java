package edu.kit.ifv.mobitopp.data.local.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.local.serialiser.StationBasedCarFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.Example;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class StationBasedCarFormatTest {

	private static final String company = "company";
	private static final int stationId = 1;
	private static final int carId = 2;

	private StationBasedCarFormat format;
	private StationBasedCarSharingCar stationCar;

	@Before
	public void initialise() {
		StationBasedCarSharingOrganization owner = new StationBasedCarSharingOrganization(company);
		Zone zone = mock(Zone.class);
		Location location = Example.location;
		String name = "station";
		String parkingSpace = "parking space";
		int numberOfCars = 1;
		CarSharingStation station = new CarSharingStation(owner, zone, stationId, name, parkingSpace,
				location, numberOfCars);

		CarPosition position = new CarPosition(zone, location);
		Segment segment = Segment.MIDSIZE;
		int capacity = 3;
		float initialMileage = 0.0f;
		float fuelLevel = 1.0f;
		int maxRange = 4;
		Car car = new ConventionalCar(carId, position, segment, capacity, initialMileage, fuelLevel, maxRange);
		stationCar = new StationBasedCarSharingCar(car, owner, station);
		Collection<Car> cars = asList(car);
		Collection<StationBasedCarSharingOrganization> owners = asList(owner);
		Collection<CarSharingStation> stations = asList(station);
		format = new StationBasedCarFormat(owners, stations, cars);
	}

	@Test
	public void prepare() {
		List<String> prepared = format.prepare(stationCar);

		assertThat(prepared, contains(company, valueOf(stationId), valueOf(carId)));
	}

	@Test
	public void parse() {
		Optional<StationBasedCarSharingCar> parsed = format.parse(format.prepare(stationCar));
		
		StationBasedCarSharingCar parsedCar = parsed.get();
		assertValue(StationBasedCarSharingCar::owner, parsedCar, stationCar);
		assertValue(c -> c.station, parsedCar, stationCar);
		assertValue(StationBasedCarSharingCar::id, parsedCar, stationCar);
		assertValue(StationBasedCarSharingCar::position, parsedCar, stationCar);
		assertValue(StationBasedCarSharingCar::carSegment, parsedCar, stationCar);
		assertValue(StationBasedCarSharingCar::capacity, parsedCar, stationCar);
		assertValue(StationBasedCarSharingCar::currentMileage, parsedCar, stationCar);
		assertValue(StationBasedCarSharingCar::currentFuelLevel, parsedCar, stationCar);
		assertValue(StationBasedCarSharingCar::maxRange, parsedCar, stationCar);
	}
}
