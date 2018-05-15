package edu.kit.ifv.mobitopp.simulation.person;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class RouteSplitter {

	private final List<Connection> connections;
	private final ArrayList<PublicTransportLeg> legs;

	private Stop start;
	private Time departure;
	private Journey journey;
	private List<Connection> legConnections;
	private LegBuilder nextLeg;

	public RouteSplitter(List<Connection> connections) {
		super();
		this.connections = connections;
		legs = new ArrayList<>();
	}

	static List<PublicTransportLeg> splitInParts(PublicTransportRoute route) {
		return new RouteSplitter(route.connections()).split();
	}

	private List<PublicTransportLeg> split() {
		for (Connection connection : connections) {
			process(connection);
		}
		addLastLeg();
		return legs;
	}

	private void addLastLeg() {
		if (null == journey) {
			return;
		}
		finishLeg();
	}

	private void process(Connection connection) {
		if (journey == null) {
			startLegWith(connection);
		}
		if (!journey.equals(connection.journey())) {
			finishLeg();
			startLegWith(connection);
		}
		addToCurrent(connection);
	}

	private void addToCurrent(Connection connection) {
		legConnections.add(connection);
	}

	private void finishLeg() {
		Stop end = lastConnection().end();
		Time arrival = lastConnection().arrival();
		legs.add(newLeg(start, end, departure, arrival, journey, legConnections));
	}

	private Connection lastConnection() {
		return legConnections.get(legConnections.size() - 1);
	}

	private void startLegWith(Connection connection) {
//		nextLeg = new LegBuilder();
//		nextLeg.add(connection);
		legConnections = new ArrayList<>();
		start = connection.start();
		departure = connection.departure();
		journey = connection.journey();
	}

	private PublicTransportLeg newLeg(
			Stop start, Stop end, Time departure, Time arrival, Journey journey, List<Connection> last) {
		return new PublicTransportLeg(start, end, journey, departure, arrival, last);
	}
}
