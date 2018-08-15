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
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.Example;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;

public class ConventionalCarFormatTest {

	private static final int zoneOid = 1;

	private Zone zone;
	private ConventionalCarFormat format;
	private ConventionalCar original;
	private ZoneRepository zoneRepository;

	@Before
	public void initialise() {
		zone = mock(Zone.class);
		zoneRepository = mock(ZoneRepository.class);
		format = new ConventionalCarFormat(zoneRepository);

		when(zone.getOid()).thenReturn(zoneOid);
		when(zoneRepository.getZoneByOid(zoneOid)).thenReturn(zone);

		original = Example.conventionalCar(zone);
	}

	@Test
	public void prepareConventionalCar() {
		List<String> prepared = format.prepare(original);

		assertThat(prepared, is(equalTo(conventionalCar())));
	}

	@Test
	public void parseConventionalCar() {
		Optional<ConventionalCar> parsed = format.parse(conventionalCar());

		assertCars(parsed.get(), original);
	}

	private void assertCars(ConventionalCar actual, ConventionalCar original) {
		assertValue(ConventionalCar::id, actual, original);
		assertCarPosition(actual, original);
		assertValue(ConventionalCar::carSegment, actual, original);
		assertValue(ConventionalCar::capacity, actual, original);
		assertValue(ConventionalCar::currentMileage, actual, original);
		assertValue(ConventionalCar::currentFuelLevel, actual, original);
		assertValue(ConventionalCar::maxRange, actual, original);
	}

	private void assertCarPosition(ConventionalCar actual, ConventionalCar original) {
		CarPosition actualPosition = actual.position();
		CarPosition originalPosition = original.position();
		assertValue(CarPosition::zone, actualPosition, originalPosition);
		assertValue(CarPosition::location, actualPosition, originalPosition);
	}

	private List<String> conventionalCar() {
		return asList( 
				valueOf(Example.carId), 
				valueOf(zoneOid), 
				valueOf(Example.serialisedLocation),
				valueOf(Example.segment), 
				valueOf(Example.capacity), 
				valueOf(Example.initialMileage),
				valueOf(Example.fuelLevel), 
				valueOf(Example.maxRange) 
			);
	}
}
