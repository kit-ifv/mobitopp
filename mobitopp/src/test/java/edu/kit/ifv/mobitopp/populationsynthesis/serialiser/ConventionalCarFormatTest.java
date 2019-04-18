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
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;

public class ConventionalCarFormatTest {

	private static final int zoneOid = 1;
  private static final ZoneId zoneId = new ZoneId("1", zoneOid);

	private Zone zone;
	private ConventionalCarFormat format;
	private Car original;
	private ZoneRepository zoneRepository;

	@Before
	public void initialise() {
		zone = mock(Zone.class);
		zoneRepository = mock(ZoneRepository.class);
		format = new ConventionalCarFormat(zoneRepository);

		when(zone.getInternalId()).thenReturn(zoneId);
		when(zoneRepository.getZoneByOid(zoneOid)).thenReturn(zone);

		original = ExampleSetup.conventionalCar(zone);
	}

	@Test
	public void prepareConventionalCar() {
		List<String> prepared = format.prepare(original);

		assertThat(prepared, is(equalTo(conventionalCar())));
	}

	@Test
	public void parseConventionalCar() {
		Optional<Car> parsed = format.parse(conventionalCar());

		assertCars(parsed.get(), original);
	}

	private void assertCars(Car actual, Car original) {
		assertValue(Car::id, actual, original);
		assertCarPosition(actual, original);
		assertValue(Car::carSegment, actual, original);
		assertValue(Car::capacity, actual, original);
		assertValue(Car::currentMileage, actual, original);
		assertValue(Car::currentFuelLevel, actual, original);
		assertValue(Car::maxRange, actual, original);
	}

	private void assertCarPosition(Car actual, Car original) {
		CarPosition actualPosition = actual.position();
		CarPosition originalPosition = original.position();
		assertValue(CarPosition::zone, actualPosition, originalPosition);
		assertValue(CarPosition::location, actualPosition, originalPosition);
	}

	private List<String> conventionalCar() {
		return asList( 
				valueOf(ExampleSetup.carId), 
				valueOf(zoneOid), 
				valueOf(ExampleSetup.serialisedLocation),
				valueOf(ExampleSetup.segment), 
				valueOf(ExampleSetup.capacity), 
				valueOf(ExampleSetup.initialMileage),
				valueOf(ExampleSetup.fuelLevel), 
				valueOf(ExampleSetup.maxRange) 
			);
	}
}
