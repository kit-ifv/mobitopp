package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.Collection;

import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class TransitNetwork {

	private final Collection<Stop> stops;
	private final ConnectionSweeper connections;

	private TransitNetwork(Collection<Stop> stops, ConnectionSweeper sweeper) {
		super();
		this.stops = stops;
		this.connections = sweeper;
	}

	public static TransitNetwork createOf(Collection<Stop> stops, Connections connections) {
		assertIdsOf(stops);
		DefaultConnectionSweeper sweeper = DefaultConnectionSweeper.from(connections);
		return new TransitNetwork(stops, sweeper);
	}

	private static void assertIdsOf(Collection<Stop> stops) {
		ArrayList<Stop> internalStops = new ArrayList<>(stops);
		internalStops.sort(comparing(Stop::id));
		for (int index = 0; index < internalStops.size(); index++) {
			Stop stop = internalStops.get(index);
			if (index != stop.id()) {
				throw new IllegalArgumentException("Ids of stops must be consecutive starting at 0. Wrong id at stop: " + stop);
			}
		}
	}

	ConnectionSweeper connections() {
		return connections;
	}

	Collection<Stop> stops() {
		return stops;
	}

	boolean scanNotNeeded(Stop start, Stop end, Time time) {
		return connections.areDepartedBefore(time) || notAvailable(start, end);
	}

	private boolean notAvailable(Stop fromStart, Stop toEnd) {
		return !stops.contains(fromStart) || !stops.contains(toEnd);
	}

	public boolean scanNotNeeded(StopPaths starts, StopPaths ends, Time time) {
		return connections.areDepartedBefore(time) || notAvailable(starts, ends);
	}

	private boolean notAvailable(StopPaths startStops, StopPaths endStops) {
		return notAvailable(startStops) || notAvailable(endStops);
	}

	private boolean notAvailable(StopPaths requested) {
		return requested.stops().isEmpty() || !stops().containsAll(requested.stops());
	}

}
