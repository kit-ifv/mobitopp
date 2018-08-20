package edu.kit.ifv.mobitopp.simulation.carsharing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.MockedZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;

public class StationBasedCarSharingOrganizationTest {

	private final String name = "DUMMY";

	private StationBasedCarSharingOrganization company;
	private CarSharingStation station;

	private CarPosition position;
	private StationBasedCarSharingCar car;

	private MockedZones zones;


	@Before
	public void setUp() {
		zones = MockedZones.create();
		company = new StationBasedCarSharingOrganization(name);
		addStationToCompany();

		position = new CarPosition(someZone(), null);
		ConventionalCar realCar = new ConventionalCar(new IdSequence(), position, Car.Segment.MIDSIZE);
		car = new StationBasedCarSharingCar(
				realCar, company, station);
	}

	private void addStationToCompany() {
		station = new CarSharingStation(
										company,
										someZone(),
										0,
										"station",
										"parking space",
										new Location(new Point2D.Double(0.0,0.0),-1,0.0),
										1
							);
	}

	private Zone someZone() {
		return zones.someZone();
	}
	
	@After
	public void verifyZones() {
		zones.verify();
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

	@Test(expected=AssertionError.class)
	public void testReturnCarOtherZone() {
		assertFalse(company.isCarAvailable(someZone()));

		company.ownCar(car, someZone());

		assertTrue(company.isCarAvailable(someZone()));

		CarSharingCar bookedCar = company.bookCar(someZone());

		assertEquals(car, bookedCar);
		assertFalse(company.isCarAvailable(someZone()));

		company.returnCar(car, otherZone());
	}

	private Zone otherZone() {
		return zones.otherZone();
	}

	@Test
	public void testAddStation() {
		company.ownCar(car, someZone());

		assertTrue(company.isCarAvailable(someZone()));
		assertEquals(car, company.nextAvailableCar(someZone()));
	}

}

