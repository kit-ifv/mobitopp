package edu.kit.ifv.mobitopp.publictransport.matcher;

import org.hamcrest.Matcher;

import edu.kit.ifv.mobitopp.publictransport.matcher.connection.ArriveAt;
import edu.kit.ifv.mobitopp.publictransport.matcher.connection.DepartAt;
import edu.kit.ifv.mobitopp.publictransport.matcher.connection.DepartsBefore;
import edu.kit.ifv.mobitopp.publictransport.matcher.connection.EndAt;
import edu.kit.ifv.mobitopp.publictransport.matcher.connection.HasId;
import edu.kit.ifv.mobitopp.publictransport.matcher.connection.PartOf;
import edu.kit.ifv.mobitopp.publictransport.matcher.connection.StartAt;
import edu.kit.ifv.mobitopp.publictransport.matcher.connection.TraversePoints;
import edu.kit.ifv.mobitopp.publictransport.matcher.connection.Valid;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class ConnectionMatchers {

	public static Matcher<Connection> departsBefore(Time time) {
		return new DepartsBefore(time);
	}

	public static Matcher<Connection> valid() {
		return new Valid();
	}

	public static Matcher<Connection> traverses(RoutePoints points) {
		return new TraversePoints(points);
	}

	public static Matcher<Connection> arrivesAt(Time arrival) {
		return new ArriveAt(arrival);
	}

	public static Matcher<Connection> departsAt(Time departure) {
		return new DepartAt(departure);
	}

	public static Matcher<Connection> startsAt(Stop stop) {
		return new StartAt(stop);
	}

	public static Matcher<Connection> endsAt(Stop stop) {
		return new EndAt(stop);
	}

	public static Matcher<Connection> partOf(Journey journey) {
		return new PartOf(journey);
	}

	public static Matcher<Connection> hasId(ConnectionId id) {
		return new HasId(id);
	}

}
