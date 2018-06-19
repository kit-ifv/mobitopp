package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;

import java.awt.geom.Point2D;
import java.util.List;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.simulation.person.VehicleLeg;

public class Data {

	public static Time time(int hour, int minute) {
		return SimpleTime.ofHours(hour).plusMinutes(minute);
	}

	public static Time second(int seconds) {
		return SimpleTime.ofSeconds(seconds);
	}

	public static Point2D coordinate(float x, float y) {
		return new Point2D.Float(x, y);
	}

	public static Time oneMinuteEarlier() {
		return someTime().minus(RelativeTime.of(1, MINUTES));
	}

	public static Time someDay() {
		return someTime();
	}

	public static Time someTime() {
		return time(0, 0);
	}

	public static Time oneMinuteLater() {
		return someTime().plus(RelativeTime.of(1, MINUTES));
	}

	public static Time twoMinutesLater() {
		return oneMinuteLater().plus(RelativeTime.of(1, MINUTES));
	}

	public static Time threeMinutesLater() {
		return twoMinutesLater().plus(RelativeTime.of(1, MINUTES));
	}

	public static Time fourMinutesLater() {
		return threeMinutesLater().plus(RelativeTime.of(1, MINUTES));
	}

	public static Stop someStop() {
		return stop()
				.withExternalId(1)
				.withId(0)
				.withName("some stop")
				.with(coordinate(0, 0))
				.build();
	}

	public static Stop anotherStop() {
		return stop()
				.withExternalId(2)
				.withId(1)
				.withName("another stop")
				.with(coordinate(1, 2))
				.build();
	}

	public static Stop otherStop() {
		return stop()
				.withExternalId(3)
				.withId(2)
				.withName("other stop")
				.with(coordinate(3, 4))
				.build();
	}

	public static Stop yetAnotherStop() {
		return stop()
				.withExternalId(4)
				.withId(3)
				.withName("yet another stop")
				.with(coordinate(5, 6))
				.build();
	}

	public static Connection fromSomeToAnother() {
		return connection()
				.withId(1)
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(someTime())
				.arrivesAt(oneMinuteLater())
				.build();
	}

	public static Connection laterFromSomeToAnother() {
		return connection()
				.withId(2)
				.startsAt(someStop())
				.endsAt(anotherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	public static Connection fromOtherToAnother() {
		return connection()
				.withId(3)
				.startsAt(otherStop())
				.endsAt(anotherStop())
				.departsAt(someTime())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	public static Connection fromAnotherToOther() {
		return connection()
				.withId(4)
				.startsAt(anotherStop())
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.build();
	}

	public static Connection laterFromAnotherToOther() {
		return connection()
				.withId(5)
				.startsAt(anotherStop())
				.endsAt(otherStop())
				.departsAt(twoMinutesLater())
				.arrivesAt(threeMinutesLater())
				.build();
	}
	
	public static Connection fromAnotherToOtherByOtherJourney() {
		return connection()
				.withId(6)
				.startsAt(anotherStop())
				.endsAt(otherStop())
				.departsAt(oneMinuteLater())
				.arrivesAt(twoMinutesLater())
				.partOf(otherJourney())
				.build();
	}

	private static Journey otherJourney() {
		return journey().withId(1).build();
	}
	
	public static PublicTransportLeg someLeg() {
		return newLeg(fromSomeToAnother());
	}
	
	public static PublicTransportLeg newLeg(Connection connection) {
		Stop start = connection.start();
		Stop end = connection.end();
		Journey journey = connection.journey();
		Time departure = connection.departure();
		Time arrival = connection.arrival();
		List<Connection> connections = asList(connection);
		return new VehicleLeg(start, end, journey, departure, arrival, connections);
	}
}
