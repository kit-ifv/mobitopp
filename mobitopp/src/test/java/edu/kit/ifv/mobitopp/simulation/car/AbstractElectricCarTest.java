package edu.kit.ifv.mobitopp.simulation.car;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.time.Time;

public class AbstractElectricCarTest {

	private static final Time someDate = Data.someTime();
	private static final float empty = 0.0f;
	private static final float full = 1.0f;
	private static final float chargeAlways = 1.0f;
	private static final float chargeInHalf = 0.5f;
	private static final float belowChargeInHalf = chargeInHalf - 0.1f;
	private static final float aboveChargeInHalf = chargeInHalf + 0.1f;

	private IdSequence ids;
	private CarPosition position;
	private Segment segment;
	private int capacity;
	private float initialMileage;
	private float fuelLevel;
	private float electricRange;
	private float conventionalRange;
	private float batteryCapacity;
	private Zone zone;
	private Location location;
	private ChargingDataForZone chargingData;
	private Person driver;
	private ActivityIfc activity;

	@Before
	public void initialise() throws Exception {
		ids = mock(IdSequence.class);
		zone = mock(Zone.class);
		chargingData = mock(ChargingDataForZone.class);
		position = new CarPosition(zone, location);
		driver = mock(Person.class);
		activity = mock(ActivityIfc.class);

		when(driver.nextActivity()).thenReturn(activity);
		when(zone.charging()).thenReturn(chargingData);
		when(chargingData.canElectricCarCharge(any(), any())).thenReturn(true);
	}

	@Test
	public void startsChargingEmptyCar() throws Exception {
		Car car = newCar(empty, chargeAlways);

		car.stop(someDate, position);

		verifyCharged();
	}

	@Test
	public void doesNotChargeFullCar() throws Exception {
		Car car = newCar(full, chargeAlways);

		car.stop(someDate, position);

		verifyNotCharged();
	}

	@Test
	public void chargesBelowMinimumChargingLevel() throws Exception {
		Car car = newCar(belowChargeInHalf, chargeInHalf);

		car.stop(someDate, position);

		verifyCharged();
	}

	@Test
	public void doesNotChargeAboveMinimumChargingLevel() throws Exception {
		Car car = newCar(aboveChargeInHalf, chargeInHalf);

		car.stop(someDate, position);

		verifyNotCharged();
	}

	@Test
	public void doesNotChargeAtMinimumChargingLevel() throws Exception {
		Car car = newCar(chargeInHalf, chargeInHalf);

		car.stop(someDate, position);

		verifyNotCharged();
	}

	private void verifyCharged() {
		verify(chargingData).startCharging(any(), any(), any(), any(), anyFloat());
	}

	private void verifyNotCharged() {
		verify(chargingData, times(0)).startCharging(any(), any(), any(), any(), anyFloat());
	}

	private Car newCar(float batteryLevel, float minimumChargingLevel) {
		Car car = new AbstractElectricCar(ids, position, segment, capacity, initialMileage,
				batteryLevel, fuelLevel, electricRange, conventionalRange, batteryCapacity,
				minimumChargingLevel) {

			private static final long serialVersionUID = 1L;

			@Override
			public float remainingRange() {
				return 0;
			}

			@Override
			public float maxRange() {
				return 0;
			}

			@Override
			public String getType() {
				return null;
			}

			@Override
			public String electricModeAsChar() {
				return null;
			}

			@Override
			public float effectiveRange() {
				return 0.0f;
			}

			@Override
			public void driveDistance(float distanceKm) {
			}
		};
		car.use(driver, someDate);
		car.start(someDate);
		return car;
	}
}
