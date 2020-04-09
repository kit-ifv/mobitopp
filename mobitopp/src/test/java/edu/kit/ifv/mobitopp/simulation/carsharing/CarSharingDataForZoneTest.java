package edu.kit.ifv.mobitopp.simulation.carsharing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household_Stub;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person_Stub;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;

public class CarSharingDataForZoneTest  {

	private final String stationBasedName = "station based";
	private final String freeFloatingName = "free floating";

	private ExampleZones zones;

	private	List<FreeFloatingCarSharingOrganization> freeFloatingCarSharingCompanies;
	private	List<StationBasedCarSharingOrganization> stationBasedCarSharingCompanies;
	private	Map<String,List<CarSharingStation>> carSharingStations;
	private	Map<String,List<CarSharingStation>> noCarSharingStations;
	private	FreeFloatingCarSharingOrganization freeFloatingCarSharing;
	private	StationBasedCarSharingOrganization stationBasedCarSharing;

	private CarSharingStation station;

	private	Map<String, Boolean> freeFloatingArea;
	private Map<String, Integer> freeFloatingCars;
	private Map<String, Float> carsharingcarDensities;

	private CarSharingDataForZone carSharing;
	private CarSharingDataForZone otherCarSharing;
	private CarSharingDataForZone noCarSharing;
	private CarSharingDataForZone noFreeFloating;

	private CarSharingPerson customer;
	private CarSharingPerson noCustomer;

	private StationBasedCarSharingCar stationBasedCar;
	private DefaultCarSharingCar freeFloatingCar;

	@BeforeEach
	public void setUp() {
		zones = ExampleZones.create();

		freeFloatingCarSharing = new FreeFloatingCarSharingOrganization(freeFloatingName);
		stationBasedCarSharing = new StationBasedCarSharingOrganization(stationBasedName);

		freeFloatingCarSharingCompanies = new ArrayList<FreeFloatingCarSharingOrganization>();
		freeFloatingCarSharingCompanies.add(freeFloatingCarSharing);

		stationBasedCarSharingCompanies = new ArrayList<StationBasedCarSharingOrganization>();
		stationBasedCarSharingCompanies.add(stationBasedCarSharing);

		station = new CarSharingStation( stationBasedCarSharing, someZone(),
													1, "dummy", "dummy", new Location(new Point2D.Double(0.0, 0.0),-1,0.0),	
													1
							);

		carSharingStations = new HashMap<String,List<CarSharingStation>>();
		carSharingStations.put(stationBasedName, new ArrayList<CarSharingStation>(Arrays.asList(station)));

		noCarSharingStations = new HashMap<String,List<CarSharingStation>>();
		noCarSharingStations.put(stationBasedName, new ArrayList<CarSharingStation>());


		freeFloatingArea = new HashMap<String, Boolean>();
		freeFloatingCars = new HashMap<String, Integer>();
		carsharingcarDensities = new HashMap<String, Float>();
		carsharingcarDensities.put(freeFloatingName, 2.0f);

		freeFloatingArea.put(freeFloatingName, true);
		freeFloatingCars.put(freeFloatingName, 3);

		carSharing = new CarSharingDataForZone(
			someZone(),
			stationBasedCarSharingCompanies,
			carSharingStations,
			freeFloatingCarSharingCompanies,
			freeFloatingArea,
			freeFloatingCars,
			carsharingcarDensities
		);

		otherCarSharing = new CarSharingDataForZone(
			otherZone(),
			stationBasedCarSharingCompanies,
			noCarSharingStations,
			freeFloatingCarSharingCompanies,
			freeFloatingArea,
			freeFloatingCars,
			carsharingcarDensities
		);

		noCarSharing = new CarSharingDataForZone(
			someZone(),
			new ArrayList<StationBasedCarSharingOrganization>(),
			noCarSharingStations,
			new ArrayList<FreeFloatingCarSharingOrganization>(),
			freeFloatingArea,
			freeFloatingCars,
			carsharingcarDensities
		);

		noFreeFloating = new CarSharingDataForZone(
			someZone(),
			new ArrayList<StationBasedCarSharingOrganization>(),
			noCarSharingStations,
			new ArrayList<FreeFloatingCarSharingOrganization>(),
			new HashMap<String, Boolean>(),
			new HashMap<String, Integer>(),
			carsharingcarDensities
		);

		Map<String,Boolean> customership = new HashMap<String,Boolean>();
		customership.put(stationBasedName, true);
		customership.put(freeFloatingName, true);

		Map<String,Boolean> noCustomership = new HashMap<String,Boolean>();
		noCustomership.put(stationBasedName, false);
		noCustomership.put(freeFloatingName, false);

		customer = new EmobilityPerson(
												new Person_Stub(1, new Household_Stub(1)),
												0.0f,
												EmobilityPerson.PublicChargingInfluencesDestinationChoice.NEVER,
												customership
											);

		noCustomer = new EmobilityPerson(
												new Person_Stub(1, new Household_Stub(1)),
												0.0f,
												EmobilityPerson.PublicChargingInfluencesDestinationChoice.NEVER,
												noCustomership
											);

		CarPosition position = new CarPosition(someZone(), new Example().location());

		freeFloatingCar = new DefaultCarSharingCar(
												new ConventionalCar(new IdSequence(), position, Car.Segment.MIDSIZE),
												freeFloatingCarSharing
											);

		freeFloatingCarSharing.ownCar(freeFloatingCar, someZone());

		stationBasedCar = new StationBasedCarSharingCar(
												new ConventionalCar(new IdSequence(), position, Car.Segment.MIDSIZE),
												stationBasedCarSharing, station
											);

		stationBasedCarSharing.ownCar(stationBasedCar, someZone());
	}

	private Zone someZone() {
		return zones.someZone();
	}

	private Zone otherZone() {
		return zones.otherZone();
	}

	@Test
	public void testConstructor() {

		assertNotNull(carSharing);
	}

	@Test
	public void testIsStationBasedCarSharingAvailable() {

		assertTrue(carSharing.isStationBasedCarSharingCarAvailable(customer));
		assertFalse(noCarSharing.isStationBasedCarSharingCarAvailable(customer));

		assertFalse(carSharing.isStationBasedCarSharingCarAvailable(noCustomer));
		assertFalse(noCarSharing.isStationBasedCarSharingCarAvailable(noCustomer));
	}

	@Test
	public void testIsFreeFloatingCarSharingAvailable() {

		assertTrue(carSharing.isFreeFloatingCarSharingCarAvailable(customer));
		assertFalse(noCarSharing.isFreeFloatingCarSharingCarAvailable(customer));

		assertFalse(carSharing.isFreeFloatingCarSharingCarAvailable(noCustomer));
		assertFalse(noCarSharing.isFreeFloatingCarSharingCarAvailable(noCustomer));
	}

	@Test
	public void testIsFreeFloatingZone() {

		assertTrue(carSharing.isFreeFloatingZone(freeFloatingCar));
		assertFalse(carSharing.isFreeFloatingZone(stationBasedCar));

		assertTrue(otherCarSharing.isFreeFloatingZone(freeFloatingCar));
		assertFalse(otherCarSharing.isFreeFloatingZone(stationBasedCar));

		assertTrue(noCarSharing.isFreeFloatingZone(freeFloatingCar));
		assertFalse(noCarSharing.isFreeFloatingZone(stationBasedCar));

		assertFalse(noFreeFloating.isFreeFloatingZone(freeFloatingCar));
		assertFalse(noFreeFloating.isFreeFloatingZone(stationBasedCar));
	}
	
	@Test
	public void isFreeFloatingZoneOfCompany() {
		assertTrue(carSharing.isFreeFloatingZone(freeFloatingName));
		assertFalse(carSharing.isFreeFloatingZone(stationBasedName));
	}

	@Test
	public void testStationBasedCarSharingCompanies() {

		List<StationBasedCarSharingOrganization> companies = carSharing.stationBasedCarSharingCompanies();

		assertNotNull(companies);
		assertFalse(companies.isEmpty());
		assertEquals(1, companies.size());
		assertTrue(companies.contains(stationBasedCarSharing));
	}

	@Test
	public void testCarSharingStations() {

		List<CarSharingStation> stations = carSharing.carSharingStations(stationBasedName, someZone());

		assertNotNull(stations);
		assertEquals(carSharingStations.get(stationBasedName).size(), stations.size());
		assertTrue(stations.containsAll(carSharingStations.get(stationBasedName)));
		assertTrue(carSharingStations.get(stationBasedName).containsAll(stations));
	}
	
	@Test
	void returnsDefaultValueIfDensityIsMissing() throws Exception {
		float density = carSharing.carsharingcarDensity(stationBasedName);
		
		assertThat(density).isCloseTo(CarSharingDataForZone.defaultDensity, offset(1e-6f));
	}
	
	@Test
	void returnDensity() throws Exception {
		float density = carSharing.carsharingcarDensity(freeFloatingName);
		
		assertThat(density).isCloseTo(2.0f, offset(1e-6f));
	}

}


