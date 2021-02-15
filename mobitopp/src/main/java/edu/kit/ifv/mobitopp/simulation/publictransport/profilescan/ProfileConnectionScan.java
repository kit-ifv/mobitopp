package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.ScannedRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.StopPaths;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfileConnectionScan implements RouteSearch {

	private final Store store;

	public ProfileConnectionScan(Store store) {
		super();
		this.store = store;
	}

	public static ProfileConnectionScan from(
			Collection<Connection> connections, Collection<Stop> stops, Store store) {
		ProfileBuilder builder = ProfileBuilder.from(connections);
		stops.parallelStream().map(builder::buildUpTo).forEach(store::save);
		return new ProfileConnectionScan(store);
	}

	public Optional<Time> find(Stop start, Stop end, Time atTime) {
		return profileTo(end, atTime).from(start).arrivalFor(atTime);
	}

	@Override
	public Optional<PublicTransportRoute> findRoute(Stop fromStart, Stop toEnd, Time atTime) {
		Optional<Time> arrival = find(fromStart, toEnd, atTime);
		if (!arrival.isPresent()) {
			return Optional.empty();
		}
		List<Connection> connections = connections(fromStart, atTime, toEnd);
		return Optional.of(new ScannedRoute(fromStart, toEnd, atTime, arrival.get(), connections));
	}

	List<Connection> connections(Stop start, Time time, Stop end) {
		ArrivalTimeFunction function = profileTo(end, time).from(start);
		Connection connection = function.connectionFor(time).get();

		Optional<Time> possibleArrivalAtTarget = function.arrivalFor(time);
		if(!possibleArrivalAtTarget.isPresent()) {
			return emptyList();
		}
		Time arrivalAtTarget = possibleArrivalAtTarget.get();
		Connection next = connection;
		List<Connection> connections = new ArrayList<>();
		while (!end.equals(next.end())) {
			if (connections.contains(next)){
				throw warn(new RuntimeException("Duplicate connection on route"), log);
			}
			connections.add(next);
			next = nextConnection(end, arrivalAtTarget, next);
		}
		connections.add(next);
		return connections;
	}

	private Connection nextConnection(Stop target, Time arrivalAtTarget, Connection last) {
		Stop stopover = last.end();
		Time nextDeparture = last.arrival();
		Optional<Connection> next = nextInVehicle(target, arrivalAtTarget, last);
		if (next.isPresent()) {
			return next.get();
		}
		Optional<Connection> footpath = footpathFrom(stopover, target, nextDeparture,
				arrivalAtTarget);
		if (footpath.isPresent()) {
			return footpath.get();
		}
		return stayInJourneyOf(last);
	}

	private static Connection stayInJourneyOf(Connection last) {
		return last.journey().connections().nextAfter(last);
	}

	private Optional<Connection> nextInVehicle(Stop end, Time arrivalAtTarget, Connection last) {
		Stop stopover = last.end();
		Time nextDeparture = last.arrival();
		Profile nextProfile = profileTo(end, nextDeparture);
		ArrivalTimeFunction timeFunction = nextProfile.from(stopover);
		Optional<Connection> possibleConnection = timeFunction.connectionFor(nextDeparture);
		boolean considerChangetime = considerChangeTime(last, possibleConnection);
		if (considerChangetime) {
			Time stopoverDeparture = stopover.addChangeTimeTo(last.arrival());
			Profile stopoverProfile = profileTo(end, stopoverDeparture);
			ArrivalTimeFunction fromStopover = stopoverProfile.from(stopover);
			Optional<Connection> nextConnection = fromStopover.connectionFor(stopoverDeparture);
			if (fromStopover.arrivalFor(stopoverDeparture).map(arrivalAtTarget::equals).orElse(false)) {
				return nextConnection;
			}
		}
		if (timeFunction.arrivalFor(nextDeparture).map(arrivalAtTarget::equals).orElse(false)) {
			return possibleConnection;
		}
		return Optional.empty();
	}

	boolean considerChangeTime(Connection last, Optional<Connection> possibleConnection) {
		if (!possibleConnection.isPresent()) {
			return false;
		}
		Connection candidate = possibleConnection.get();
		return !last.journey().equals(candidate.journey());
	}

	private Profile profileTo(Stop stop, Time atTime) {
		return store.profileTo(stop, atTime);
	}

	private Optional<Connection> footpathFrom(
			Stop stop, Stop target, Time departure, Time arrivalAtTarget) {
		Optional<Connection> neighbourIsTarget = neighbourIsTarget(stop, target, departure,
				arrivalAtTarget);
		if (neighbourIsTarget.isPresent()) {
			return neighbourIsTarget;
		}
		Optional<Connection> neighbourIsStopover = neighbourIsStopover(stop, target, departure,
				arrivalAtTarget);
		if (neighbourIsStopover.isPresent()) {
			return neighbourIsStopover;
		}
		return Optional.empty();
	}

	private Optional<Connection> neighbourIsStopover(
			Stop stop, Stop target, Time departure, Time arrivalAtTarget) {
		for (Stop neighbour : stop.neighbours()) {
			Optional<Time> departureAtNeighbour = stop.arrivalAt(neighbour, departure);
			if (!departureAtNeighbour.isPresent()) {
				continue;
			}
			ArrivalTimeFunction from = profileTo(target, departureAtNeighbour.get()).from(neighbour);
			Optional<Time> arrival = departureAtNeighbour.flatMap(time -> from.arrivalFor(time));
			if (arrival.map(arrivalAtTarget::equals).orElse(false)) {
				return transferConnection(stop, neighbour, departure);
			}
		}
		return Optional.empty();
	}

	private static Optional<Connection> transferConnection(Stop stop, Stop neighbour, Time departure) {
		return stop.arrivalAt(neighbour, departure).map(
				arrival -> Connection.byFootFrom(stop, neighbour, departure, arrival));
	}
	
	private Optional<Connection> neighbourIsTarget(
			Stop stop, Stop target, Time departure, Time arrivalAtTarget) {
		for (Stop neighbour : stop.neighbours()) {
			if (neighbour.equals(target)
					&& stop.arrivalAt(neighbour, departure).map(arrivalAtTarget::equals).orElse(false)) {
				return transferConnection(stop, neighbour, departure);
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<PublicTransportRoute> findRoute(
			StopPaths startStops, StopPaths endStops, Time time) {
		Stop earliestStart = null;
		Stop earliestEnd = null;
		Time arrival = Time.future;
		for (Stop end : endStops.stops()) {
			for (Stop start : startStops.stops()) {
				Optional<Time> current = find(start, end, time);
				if (current.isPresent() && arrival.isAfter(current.get())) {
					arrival = current.get();
					earliestStart = start;
					earliestEnd = end;
				}
			}
		}
		if (earliestStart == null || earliestEnd == null) {
			return Optional.empty();
		}
		return findRoute(earliestStart, earliestEnd, time);
	}

}
