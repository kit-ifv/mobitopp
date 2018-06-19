package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Collections.emptyList;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ConnectionBuilder {

	private static final ConnectionId defaultId = ConnectionId.of(0);
	private static final Time defaultDeparture = Data.someTime();
	private static final Time defaultArrival = defaultDeparture.plus(RelativeTime.of(1, MINUTES));
	private static final Stop defaultStart = someStop();
	private static final Stop defaultEnd = anotherStop();
	private static final Journey defaultJourney = journey().build();
	private static final List<Point2D> defaultPoints = emptyList();

	private ConnectionId id;
	private Stop start;
	private Stop end;
	private Time departure;
	private Time arrival;
	private Journey journey;
	private List<Point2D> points;

	private ConnectionBuilder() {
		super();
		id = defaultId;
		start = defaultStart;
		end = defaultEnd;
		departure = defaultDeparture;
		arrival = defaultArrival;
		journey = defaultJourney;
		points = defaultPoints;
	}

	private ConnectionBuilder(ConnectionBuilder connectionBuilder) {
		super();
		id = connectionBuilder.id;
		start = connectionBuilder.start;
		end = connectionBuilder.end;
		departure = connectionBuilder.departure;
		arrival = connectionBuilder.arrival;
		journey = connectionBuilder.journey;
		points = connectionBuilder.points;
	}

	public static ConnectionBuilder connection() {
		return new ConnectionBuilder();
	}

	public Connection build() {
		RoutePoints route = RoutePoints.from(start, end, points);
		return Connection.from(id, start, end, departure, arrival, journey, route );
	}

	public ConnectionBuilder withId(int id) {
		this.id = ConnectionId.of(id);
		return this;
	}

	public ConnectionBuilder withId(ConnectionId id) {
		this.id = id;
		return this;
	}

	public ConnectionBuilder with(List<Point2D> points) {
		this.points = new ArrayList<>(points);
		return this;
	}

	public ConnectionBuilder departsAt(Time departure) {
		this.departure = departure;
		return this;
	}

	public ConnectionBuilder arrivesAt(Time arrival) {
		this.arrival = arrival;
		return this;
	}

	public ConnectionBuilder startsAt(Stop start) {
		this.start = start;
		return this;
	}

	public ConnectionBuilder startsAt(Point2D location) {
		start = stop().with(location).build();
		return this;
	}

	public ConnectionBuilder endsAt(Stop end) {
		this.end = end;
		return this;
	}

	public ConnectionBuilder endsAt(Point2D location) {
		end = stop().with(location).build();
		return this;
	}

	public ConnectionBuilder partOf(Journey journey) {
		this.journey = journey;
		return this;
	}

	public ConnectionBuilder departsAndArrivesAt(Time time) {
		return departsAt(time).arrivesAt(time);
	}

	public ConnectionBuilder but() {
		return new ConnectionBuilder(this);
	}

}
