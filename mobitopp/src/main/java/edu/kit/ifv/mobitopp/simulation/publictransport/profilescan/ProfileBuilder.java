package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.time.Time.future;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ProfileBuilder {

	private final List<Connection> connections;

	private ProfileBuilder(List<Connection> connections) {
		super();
		this.connections = connections;
	}

	static ProfileBuilder from(Connection... connections) {
		return from(asList(connections));
	}

	public static ProfileBuilder from(Collection<Connection> connections) {
		ArrayList<Connection> ordered = new ArrayList<>(connections);
		ordered.sort(comparing(Connection::departure)
				.thenComparing(Connection::arrival)
				.thenComparing(Connection::positionInJourney)
				.reversed());
		return new ProfileBuilder(ordered);
	}

	public Profile buildUpTo(List<Stop> targets) {
		Profile toTargetWithoutTransfer = new Profile(null);
		TransferProfile withTransfer = new TransferProfile();
		HashMap<Journey, Time> trips = new HashMap<>();
		for (Connection connection : connections) {
			Stop start = connection.start();
			if (targets.contains(start)) {
				continue;
			}
			Time arrivalByFoot = arrivalByFoot(targets, connection);
			Time arrivalViaTrip = arrivalViaTrip(trips, connection);
			Time arrivalViaStopover = arrivalViaStopover(withTransfer, connection);

			Time arrivalAtTarget = minOf(arrivalByFoot, arrivalViaTrip, arrivalViaStopover);
			Journey journey = connection.journey();
			trips.put(journey, arrivalAtTarget);
			if (isValid(arrivalAtTarget)) {
				ArrivalTimeFunction functionWithoutTransfer = toTargetWithoutTransfer.from(start);
				Time departure = connection.departure();
				FunctionEntry newEntry = new FunctionEntry(departure, arrivalAtTarget, connection);
				if (functionWithoutTransfer.update(newEntry)) {
					withTransfer.updateTransfer(connection, arrivalAtTarget);
					toTargetWithoutTransfer.update(start, functionWithoutTransfer);
				}
			}
		}
		return toTargetWithoutTransfer;
	}

	public Profile buildUpTo(Stop target) {
		Profile toTargetWithoutTransfer = new Profile(target);
		TransferProfile withTransfer = new TransferProfile();
		HashMap<Journey, Time> trips = new HashMap<>();
		for (Connection connection : connections) {
			Stop start = connection.start();
			if (start.equals(target)) {
				continue;
			}
			Time arrivalByFoot = arrivalByFoot(target, connection);
			Time arrivalViaTrip = arrivalViaTrip(trips, connection);
			Time arrivalViaStopover = arrivalViaStopover(withTransfer, connection);

			Time arrivalAtTarget = minOf(arrivalByFoot, arrivalViaTrip, arrivalViaStopover);
			Journey journey = connection.journey();
			trips.put(journey, arrivalAtTarget);
			if (isValid(arrivalAtTarget)) {
				ArrivalTimeFunction functionWithoutTransfer = toTargetWithoutTransfer.from(start);
				Time departure = connection.departure();
				FunctionEntry newEntry = new FunctionEntry(departure, arrivalAtTarget, connection);
				if (functionWithoutTransfer.update(newEntry)) {
					withTransfer.updateTransfer(connection, arrivalAtTarget);
					toTargetWithoutTransfer.update(start, functionWithoutTransfer);
				}
			}
		}
		return toTargetWithoutTransfer;
	}

	private static boolean isValid(Time arrivalAtTarget) {
		return null != arrivalAtTarget && !future.equals(arrivalAtTarget);
	}

	private Time arrivalViaTrip(HashMap<Journey, Time> trips, Connection connection) {
		return trips.getOrDefault(connection.journey(), future);
	}

	private Time arrivalByFoot(List<Stop> targets, Connection connection) {
		return targets.contains(connection.end()) ? connection.arrival() : Time.future;
	}

	private Time arrivalByFoot(Stop target, Connection connection) {
		return target.equals(connection.end()) ? connection.arrival() : walkTime(connection, target);
	}

	private Time walkTime(Connection connection, Stop target) {
		Optional<RelativeTime> walkTimeToTarget = connection.end().neighbours().walkTimeTo(target);
		Time arrival = connection.arrival();
		return walkTimeToTarget.map(arrival::plus).orElse(future);
	}

	private Time arrivalViaStopover(TransferProfile profile, Connection connection) {
		return profile.from(connection.end()).arrivalFor(connection.arrival()).orElse(future);
	}

	private Time minOf(Time arrivalByFoot, Time arrivalViaTrip, Time arrivalViaStopover) {
		if (arrivalByFoot.isBefore(arrivalViaStopover)) {
			if (arrivalByFoot.isBefore(arrivalViaTrip)) {
				return arrivalByFoot;
			}
			return arrivalViaTrip;
		}
		return arrivalViaStopover.isBefore(arrivalViaTrip) ? arrivalViaStopover : arrivalViaTrip;
	}

}
