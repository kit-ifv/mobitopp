package edu.kit.ifv.mobitopp.simulation.person;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;

public class RouteSplitter {

	private final List<Connection> connections;
	private final ArrayList<PublicTransportLeg> legs;
	private LegBuilder nextLeg;

	public RouteSplitter(List<Connection> connections) {
		super();
		this.connections = connections;
		legs = new ArrayList<>();
		nextLeg = new LegBuilder();
	}

	static List<PublicTransportLeg> splitInParts(PublicTransportRoute route) {
		return new RouteSplitter(route.connections()).split();
	}

	private List<PublicTransportLeg> split() {
		if (connections.isEmpty()) {
			return emptyList();
		}
		for (Connection connection : connections) {
			process(connection);
		}
		addLastLeg();
		return legs;
	}

	private void addLastLeg() {
		finishLeg();
	}

	private void process(Connection connection) {
		if (nextLeg.needsToSplit(connection)) {
			finishLeg();
			startLegWith(connection);
			return;
		}
		addToCurrent(connection);
	}

	private void addToCurrent(Connection connection) {
		nextLeg.add(connection);
	}

	private void finishLeg() {
		nextLeg.createLeg(legs::add);
	}

	private void startLegWith(Connection connection) {
		nextLeg = new LegBuilder();
		nextLeg.startWith(connection);
	}
}
