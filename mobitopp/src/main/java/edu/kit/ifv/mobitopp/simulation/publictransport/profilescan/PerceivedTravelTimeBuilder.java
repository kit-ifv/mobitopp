package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class PerceivedTravelTimeBuilder extends ProfileBuilder {

	private final RelativeTime transferPenalty;

	protected PerceivedTravelTimeBuilder(List<Connection> connections, RelativeTime transferPenalty) {
		super(connections);
		this.transferPenalty = transferPenalty;
	}

	static ProfileBuilder from(RelativeTime transferPenalty, Connection... connections) {
		return from(transferPenalty, asList(connections));
	}

	public static ProfileBuilder from(RelativeTime transferPenalty, Collection<Connection> connections) {
		ArrayList<Connection> ordered = new ArrayList<>(connections);
		ordered
				.sort(comparing(Connection::departure)
						.thenComparing(Connection::arrival)
						.thenComparing(Connection::positionInJourney)
						.reversed());
		return new PerceivedTravelTimeBuilder(ordered, transferPenalty);
	}

	@Override
	protected void update(
			Profile toTargetWithoutTransfer, TransferProfile withTransfer, FunctionEntry newEntry) {
		Connection connection = newEntry.connection();
		Stop start = connection.start();
		ArrivalTimeFunction functionWithoutTransfer = toTargetWithoutTransfer.from(start);
		FunctionEntry updatedEntry = calculatePerceivedTraveltime(toTargetWithoutTransfer, newEntry);
		if (functionWithoutTransfer.update(updatedEntry)) {
			withTransfer.updateTransfer(connection, updatedEntry.arrivalAtTarget());
			toTargetWithoutTransfer.update(start, functionWithoutTransfer);
		}
	}

	private FunctionEntry calculatePerceivedTraveltime(
			Profile toTargetWithoutTransfer, FunctionEntry newEntry) {
		Connection connection = newEntry.connection();
		Stop end = connection.end();
		Time arrival = connection.arrival();
		Optional<Connection> nextConnection = toTargetWithoutTransfer.from(end).connectionFor(arrival);
		if (nextConnection.isPresent()) {
			RelativeTime penalty = nextConnection
					.filter(c -> c.journey().equals(connection.journey()))
					.map(c -> RelativeTime.ZERO)
					.orElse(transferPenalty);
			Time updatedArrival = newEntry.arrivalAtTarget().plus(penalty);
			return new FunctionEntry(newEntry.departure(), updatedArrival, connection);
		}
		return newEntry;
	}

}
