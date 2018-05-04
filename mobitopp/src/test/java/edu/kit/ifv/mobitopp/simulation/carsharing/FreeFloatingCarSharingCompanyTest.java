package edu.kit.ifv.mobitopp.simulation.carsharing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.Before;

import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.DefaultCarSharingCar;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;

import java.util.HashMap;


public class FreeFloatingCarSharingCompanyTest {

	private final String name = "DUMMY";

	private ExampleZones zones;
	private FreeFloatingCarSharingOrganization company;

	private CarPosition position;
	private CarSharingCar car;

	@Before
	public void setUp() {
		zones = ExampleZones.create();

		HashMap<String, Boolean> freeFloatingArea = new HashMap<String, Boolean>();
		freeFloatingArea.put(name, true);

		someZone().setCarSharing(new CarSharingDataForZone(someZone(), freeFloatingArea));
		otherZone().setCarSharing(new CarSharingDataForZone(otherZone(), freeFloatingArea));

		company = new FreeFloatingCarSharingOrganization(name);

		position = new CarPosition(someZone(), null);
		car = new DefaultCarSharingCar(
						new ConventionalCar(new IdSequence(), position, Car.Segment.MIDSIZE),
						company
					);
	}

	private Zone someZone() {
		return zones.someZone();
	}
	
	private Zone otherZone() {
		return zones.otherZone();
	}

	@Test
	public void testConstructor() {

		assertNotNull(company);

		assertEquals(name, company.name());
	}

	@Test
	public void testIsCarAvailable() {

		assertFalse(company.isCarAvailable(someZone()));
	}

	@Test
	public void testOwnCar() {

		assertFalse(company.isCarAvailable(someZone()));

		company.ownCar(car, someZone());

		assertTrue(company.isCarAvailable(someZone()));
	}

	@Test
	public void testNextAvailableCar() {

		company.ownCar(car, someZone());

		assertTrue(company.isCarAvailable(someZone()));
		assertEquals(car, company.nextAvailableCar(someZone()));
	}

	@Test
	public void testBookCar() {

		assertFalse(company.isCarAvailable(someZone()));

		company.ownCar(car, someZone());

		assertTrue(company.isCarAvailable(someZone()));

		CarSharingCar bookedCar = company.bookCar(someZone());

		assertEquals(car, bookedCar);
		assertFalse(company.isCarAvailable(someZone()));
	}

	@Test
	public void testReturnCarSameZone() {

		assertFalse(company.isCarAvailable(someZone()));

		company.ownCar(car, someZone());

		assertTrue(company.isCarAvailable(someZone()));

		CarSharingCar bookedCar = company.bookCar(someZone());

		assertEquals(car, bookedCar);
		assertFalse(company.isCarAvailable(someZone()));

		company.returnCar(car, someZone());

		assertTrue(company.isCarAvailable(someZone()));
		assertEquals(bookedCar, company.nextAvailableCar(someZone()));
	}

	@Test
	public void testReturnCarOtherZone() {

		assertFalse(company.isCarAvailable(someZone()));
		assertFalse(company.isCarAvailable(otherZone()));

		company.ownCar(car, someZone());

		assertTrue(company.isCarAvailable(someZone()));
		assertFalse(company.isCarAvailable(otherZone()));

		CarSharingCar bookedCar = company.bookCar(someZone());

		assertEquals(car, bookedCar);
		assertFalse(company.isCarAvailable(someZone()));

		company.returnCar(car, otherZone());

		assertFalse(company.isCarAvailable(someZone()));
		assertTrue(company.isCarAvailable(otherZone()));
	}



}

