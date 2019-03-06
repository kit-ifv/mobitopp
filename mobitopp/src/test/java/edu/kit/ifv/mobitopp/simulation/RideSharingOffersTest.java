package edu.kit.ifv.mobitopp.simulation;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.MockedZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.time.Time;

public class RideSharingOffersTest {

	private MockedZones zones;
	private Zone someZone;
	private Zone otherZone;
	private ZoneAndLocation origin;
	private ZoneAndLocation otherOrigin;
	private ZoneAndLocation destination;
	private ZoneAndLocation otherDestination;

	private Time time;

	private Household hh1;
	private Household hh2;

	private IdSequence seq;

	private CarPosition position;

	private Person person1;
	private Person person2;

	private RideSharingOffers offers;

	private Trip trip00;
	private Trip trip05;
	private Trip trip10;
	private Trip trip15;
	private Trip trip20;
	private Trip trip25;
	private Trip trip30;

	@Before
	public void setUp() {
		zones = MockedZones.create();
		someZone = zones.someZone();
		otherZone = zones.otherZone();
		origin = createZoneAndLocationFor(someZone);
		otherOrigin = createZoneAndLocationFor(otherZone);
		destination = createZoneAndLocationFor(someZone);
		otherDestination = createZoneAndLocationFor(otherZone);

		time = Data.someTime();
		
		hh1 = new Household_Stub(1);
		hh2 = new Household_Stub(2);

		seq = new IdSequence();

		position = new CarPosition(someZone, new Example().location());

		person1 = new Person_Stub(1, hh1, new ConventionalCar(seq, position, Car.Segment.SMALL, 4, 0.0f, 1.0f, 500));
		person2 = new Person_Stub(2, hh2, new ConventionalCar(seq, position, Car.Segment.SMALL, 4, 0.0f, 1.0f, 500));
		
		offers = new RideSharingOffers();

		trip00 = new Trip_Stub("00", origin, destination, time);
		trip05 = new Trip_Stub("05", origin, destination, time.plusMinutes( 5));
		trip10 = new Trip_Stub("10", origin, destination, time.plusMinutes(10));
		trip15 = new Trip_Stub("15", origin, destination, time.plusMinutes(15));
		trip20 = new Trip_Stub("20", origin, destination, time.plusMinutes(20));
		trip25 = new Trip_Stub("25", origin, destination, time.plusMinutes(25));
		trip30 = new Trip_Stub("30", origin, destination, time.plusMinutes(30));
	}

	private ZoneAndLocation createZoneAndLocationFor(Zone zone) {
		return new ZoneAndLocation(zone, new Example().location());
	}

	@After
	public void verifyZones() {
		zones.verify();
	}

	@Test
	public void constructor() {

		RideSharingOffers rideOffers = new RideSharingOffers();

		assertEquals(0, rideOffers.size());
	}

	@Test
	public void add() {

		assertEquals(0, offers.size());

		offerAllTrips();

		assertEquals(7, offers.size());
	}

	@Test(expected=AssertionError.class)
	public void removeFail() {

		assertEquals(0, offers.size());

		offers.add(trip00, person1);
		offers.add(trip05, person1);

		offers.remove(trip00, person2);
	}

	@Test
	public void remove() {

		assertEquals(0, offers.size());

		offerAllTrips();

		offers.remove(trip00, person1);
		offers.remove(trip10, person1);
		offers.remove(trip20, person1);
		offers.remove(trip30, person1);

		assertEquals(3, offers.size());
	}

	@Test
	public void matchingTripsLate() {

		Trip request = new Trip_Stub("request", origin, destination, time.plusMinutes( 0));

		int minutes_early = 0;
		int minutes_late = 20;

		offers.add(trip00, person1);
		offers.add(trip05, person1);
		offers.add(trip10, person1);
		offers.add(trip30, person1);

		List<RideSharingOffer> matching = offers.matchingTrips(request, minutes_early, minutes_late);

		assertEquals(3, matching.size());
	}

	@Test
	public void matchingTripsEarly() {

		Trip request = new Trip_Stub("request", origin, destination, time.plusMinutes(30));

		int minutes_early = 10;
		int minutes_late = 0;

		offers.add(trip00, person1);
		offers.add(trip05, person1);
		offers.add(trip10, person1);
		offers.add(trip20, person1);
		offers.add(trip30, person1);

		List<RideSharingOffer> matching = offers.matchingTrips(request, minutes_early, minutes_late);

		assertEquals(2, matching.size());
	}


	@Test
	public void matchingTrips() {

		Trip request = new Trip_Stub("request", origin, destination, time.plusMinutes(10));

		int minutes_early = 5;
		int minutes_late = 20;

		offers.add(trip00, person1);
		offers.add(trip05, person1);
		offers.add(trip10, person1);
		offers.add(trip15, person1);

		List<RideSharingOffer> matching = offers.matchingTrips(request, minutes_early, minutes_late);

		assertEquals(3, matching.size());
	}
	
	@Test
	public void matchingDifferentOrigin() {
		Trip request = new Trip_Stub("request", otherOrigin, destination, time);

		int minutes_early = 0;
		int minutes_late = 5;

		offers.add(trip00, person1);

		List<RideSharingOffer> matching = offers.matchingTrips(request, minutes_early, minutes_late);

		assertThat(matching, is(empty()));
	}
	
	@Test
	public void matchingDifferentDestination() {
		Trip request = new Trip_Stub("request", origin, otherDestination, time);
		
		int minutes_early = 0;
		int minutes_late = 5;
		
		offers.add(trip00, person1);
		
		List<RideSharingOffer> matching = offers.matchingTrips(request, minutes_early, minutes_late);
		
		assertThat(matching, is(empty()));
	}

	@Test
	public void comparator() {
		Trip request = new Trip_Stub("request", origin, destination, time.plusMinutes(0));

		int minutes_early = 0;
		int minutes_late = 30;

		offerAllTrips();

		List<RideSharingOffer> matching = offers.matchingTrips(request, minutes_early, minutes_late);

		assertEquals(7, matching.size());


		Collections.sort(matching, RideSharingOffer.comparator(request.startDate()) );

		assertEquals(7, matching.size());

		assertEquals(trip00, matching.get(0).trip);
		assertEquals(trip30, matching.get(6).trip);
	}

	@Test
	public void comparator2() {
		Trip request = new Trip_Stub("request", origin, destination, time.plusMinutes(30));

		int minutes_early = 40;
		int minutes_late = 30;

		offerAllTrips();

		List<RideSharingOffer> matching = offers.matchingTrips(request, minutes_early, minutes_late);

		assertEquals(7, matching.size());


		Collections.sort(matching, RideSharingOffer.comparator(request.startDate()) );

		assertEquals(7, matching.size());

		assertEquals(trip30, matching.get(0).trip);
		assertEquals(trip00, matching.get(6).trip);
	}

	@Test
	public void comparatorHH() {
		Trip request = new Trip_Stub("request", origin, destination, time.plusMinutes( 0));

		int minutes_early = 0;
		int minutes_late = 30;

		offerAllTrips();

		List<RideSharingOffer> matching = offers.matchingTrips(request, minutes_early, minutes_late);

		assertEquals(7, matching.size());


		Collections.sort(matching, RideSharingOffer.comparator(request.startDate(), hh2) );

		assertEquals(7, matching.size());

		assertEquals(trip00, matching.get(0).trip);
		assertEquals(trip30, matching.get(6).trip);

		offers.remove(trip30, person1);
		offers.add(trip30, person2);

		matching = offers.matchingTrips(request, minutes_early, minutes_late);
		Collections.sort(matching, RideSharingOffer.comparator(request.startDate(), hh2) );

		assertEquals(7, matching.size());

		assertEquals(trip30, matching.get(0).trip);
		assertEquals(trip25, matching.get(6).trip);
	}

	private void offerAllTrips() {
		offers.add(trip00, person1);
		offers.add(trip05, person1);
		offers.add(trip10, person1);
		offers.add(trip15, person1);
		offers.add(trip20, person1);
		offers.add(trip25, person1);
		offers.add(trip30, person1);
	}

}
