package edu.kit.ifv.mobitopp.simulation.publictransport;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class Route implements Iterable<Stop> {

	private final List<Stop> stops;

	public Route(List<Stop> stops) {
		this.stops = new ArrayList<>(stops);
	}

	@Override
	public Iterator<Stop> iterator() {
		return stops.iterator();
	}

	public static Route from(Collection<Connection> connections) {
		if (connections.isEmpty()) {
			return new Route(emptyList());
		}
		Iterator<Connection> iterator = connections.iterator();
		ArrayList<Stop> stops = new ArrayList<>();
		Connection connection = null;
		while (iterator.hasNext()) {
			connection = iterator.next();
			stops.add(connection.start());
		}
		stops.add(connection.end());
		return new Route(stops);
	}
}
