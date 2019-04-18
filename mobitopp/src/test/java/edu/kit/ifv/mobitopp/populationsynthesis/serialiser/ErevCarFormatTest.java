package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.car.AbstractElectricCar;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ExtendedRangeElectricCar;

public class ErevCarFormatTest {
	private static final int zoneOid = 1;
	private static final ZoneId zoneId = new ZoneId("1", zoneOid);

	private Zone zone;
	private ErevCarFormat format;
	private ExtendedRangeElectricCar original;
	private ZoneRepository zoneRepository;

	@Before
	public void initialise() {
		zone = mock(Zone.class);
		zoneRepository = mock(ZoneRepository.class);
		ConventionalCarFormat conventionalCarFormat = new ConventionalCarFormat(zoneRepository);
		AbstractElectricCarFormat electricCarFormat = new AbstractElectricCarFormat(conventionalCarFormat);
		format = new ErevCarFormat(electricCarFormat);

		when(zone.getId()).thenReturn(zoneId);
		when(zoneRepository.getZoneByOid(zoneOid)).thenReturn(zone);

		original = ExampleSetup.erevCar(zone);
	}

	@Test
	public void prepareBevCar() {
		List<String> prepared = format.prepare(original);

		assertThat(prepared, is(equalTo(erevCar())));
	}

	@Test
	public void parseBevCar() {
		Optional<ExtendedRangeElectricCar> parsed = format.parse(erevCar());

		assertCars(parsed.get(), original);
	}

	private void assertCars(ExtendedRangeElectricCar actual, ExtendedRangeElectricCar original) {
		assertValue(Car::id, actual, original);
		assertCarPosition(actual, original);
		assertValue(Car::carSegment, actual, original);
		assertValue(Car::capacity, actual, original);
		assertValue(Car::currentMileage, actual, original);
		assertValue(Car::currentFuelLevel, actual, original);
		assertValue(Car::maxRange, actual, original);
		assertValue(AbstractElectricCar::currentBatteryLevel, actual, original);
		assertValue(AbstractElectricCar::electricRange, actual, original);
		assertValue(AbstractElectricCar::currentBatteryCapacity, actual, original);
		assertValue(AbstractElectricCar::minimumChargingLevel, actual, original);
		assertValue(ExtendedRangeElectricCar::fullPowerRange, actual, original);
	}

	private void assertCarPosition(Car actual, Car original) {
		CarPosition actualPosition = actual.position();
		CarPosition originalPosition = original.position();
		assertValue(CarPosition::zone, actualPosition, originalPosition);
		assertValue(CarPosition::location, actualPosition, originalPosition);
	}

	private List<String> erevCar() {
		ArrayList<String> attributes = new ArrayList<>();
		attributes.add(valueOf(ExampleSetup.carId));
		attributes.add(valueOf(zoneOid));
		attributes.add(valueOf(ExampleSetup.serialisedLocation));
		attributes.add(valueOf(ExampleSetup.segment));
		attributes.add(valueOf(ExampleSetup.capacity));
		attributes.add(valueOf(ExampleSetup.initialMileage));
		attributes.add(valueOf(ExampleSetup.conventionalFuelLevel));
		attributes.add(valueOf(ExampleSetup.conventionalRange + ExampleSetup.electricRange));
		attributes.add(valueOf(ExampleSetup.batteryLevel));
		attributes.add(valueOf(ExampleSetup.electricRange));
		attributes.add(valueOf(ExampleSetup.batteryCapacity));
		attributes.add(valueOf(ExampleSetup.minimumChargingLevel));
		attributes.add(valueOf(ExampleSetup.fullPowerRange));
		return attributes;
	}
}
