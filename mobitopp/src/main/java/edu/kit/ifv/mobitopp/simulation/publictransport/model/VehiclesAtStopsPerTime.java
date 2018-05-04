package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

class VehiclesAtStopsPerTime {

	private final Map<Time, List<StopJourney>> timeAtStop;

	private VehiclesAtStopsPerTime() {
		super();
		timeAtStop = new HashMap<>();
	}

	static VehiclesAtStopsPerTime arrivalsOf(Collection<Connection> connections) {
		return timesOf(connections, Connection::arrival, Connection::end);
	}

	static VehiclesAtStopsPerTime departuresOf(Collection<Connection> connections) {
		return timesOf(connections, Connection::departure, Connection::start);
	}

	private static VehiclesAtStopsPerTime timesOf(
			Collection<Connection> connections, Function<Connection, Time> timeOf,
			Function<Connection, Stop> stopOf) {
		VehiclesAtStopsPerTime timesAtStops = new VehiclesAtStopsPerTime();
		for (Connection connection : connections) {
			timesAtStops.addTimeAtStop(connection, timeOf, stopOf);
		}
		return timesAtStops;
	}

	private void addTimeAtStop(
			Connection connection, Function<Connection, Time> timeOf, Function<Connection, Stop> stopOf) {
		Time time = timeOf.apply(connection);
		Stop stop = stopOf.apply(connection);
		Journey journey = connection.journey();
		StopJourney atStopForJourney = new StopJourney(stop, journey);
		add(time, atStopForJourney);
	}

	private void add(Time time, StopJourney stopJourney) {
		List<StopJourney> stops = timeAtStop.getOrDefault(time, new ArrayList<>());
		stops.add(stopJourney);
		timeAtStop.put(time, stops);
	}

	void log(Time currentDate, BiConsumer<Time, List<StopJourney>> logger) {
		List<StopJourney> stopJourneys = timeAtStop.getOrDefault(currentDate, Collections.emptyList());
		logger.accept(currentDate, stopJourneys);
	}

}
