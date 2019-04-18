package edu.kit.ifv.mobitopp.simulation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;


public class CarTest {

	private final int initialMileage = 50;
	private final float initialFuelLevel = 0.5f;
	private final int maxRange = 200;

	private final int drivingDistance = 50;

	private Zone zone;
	private CarPosition position;
	private Car car;

	@Before
	public void setUp() {
		zone = mock(Zone.class);
		when(zone.getInternalId()).thenReturn(new ZoneId("1", 1));
		position = new CarPosition(zone, new Example().location());
		car = new ConventionalCar(new IdSequence(), position, null, 4, initialMileage, initialFuelLevel, maxRange);
	}

	@Test
	public void testCurrentConstructor() {
		assertEquals("failure - initial mileage wrong", initialMileage, car.currentMileage(), 0.0001f);
		assertEquals("failure - initial fuel level wrong", initialFuelLevel, car.currentFuelLevel(), 0.0001f);
		assertEquals("failure - maxRange wrong", maxRange, car.maxRange());
		assertEquals("failure - remainingRange wrong", maxRange*initialFuelLevel, car.remainingRange(), 0.0001f);
	}

	@Test
	public void testCurrentMileage() {
		float mileage = car.currentMileage();

		car.driveDistance(drivingDistance);

		assertEquals("failure - mileage wrong", mileage+drivingDistance, car.currentMileage(), 0.0001f);
	}

	@Test
	public void testRemainingDistance() {
		car.driveDistance(drivingDistance);

		assertEquals("failure - remainingRange wrong", 
									maxRange*initialFuelLevel-drivingDistance, car.remainingRange(), 0.0001f);
	}

	@Test
	public void testFuelLevel() {
		float fuelLevel = car.currentFuelLevel();

		float distance = 0.5f * car.maxRange() * fuelLevel;

		car.driveDistance(distance);

		assertEquals("failure - mileage wrong", initialMileage+distance, car.currentMileage(), 0.0001f);
		assertEquals("failure - fuel level wrong", 0.5*fuelLevel, car.currentFuelLevel(), 0.0001f);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRefuelNegative() {
		car.refuel(-2.0f);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRefuelToLarge() {
		car.refuel(2.0f);
	}

	@Test
	public void testRefuel() {
		car.refuel(initialFuelLevel);
		assertEquals("failure - fuel level wrong", initialFuelLevel, car.currentFuelLevel(), 0.0001f);

		car.refuel(0.0f);
		assertEquals("failure - fuel level wrong", initialFuelLevel, car.currentFuelLevel(), 0.0001f);

		car.refuel(1.5f*initialFuelLevel);
		assertEquals("failure - fuel level wrong", 1.5f*initialFuelLevel, car.currentFuelLevel(), 0.0001f);

		car.refuel(1.0f);
		assertEquals("failure - fuel level wrong", 1.0f, car.currentFuelLevel(), 0.0001f);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testDriveDistanceNegative() {
		car.driveDistance(-1.0f);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testDriveDistanceZero() {
		car.driveDistance(0.0f);
	}

	@Test
	public void testDriveDistance() {
		assertEquals("failure - initial mileage wrong", initialMileage, car.currentMileage(), 0.0001f);

		car.driveDistance(drivingDistance);
		assertEquals("failure - mileage wrong", initialMileage+drivingDistance, car.currentMileage(), 0.0001f);

		car.driveDistance(car.remainingRange());
		assertEquals("failure - mileage wrong", initialMileage+initialFuelLevel*maxRange, 
																						car.currentMileage(), 0.0001f);
	}

	@Test
	public void testRemainingRangeRounding() {
		Car mycar = new ConventionalCar(new IdSequence(), position, null, 4, 0, 1.0f, 1000);

		assertEquals("failure - remainingRange wrong", 1000, mycar.remainingRange());
	}

}
