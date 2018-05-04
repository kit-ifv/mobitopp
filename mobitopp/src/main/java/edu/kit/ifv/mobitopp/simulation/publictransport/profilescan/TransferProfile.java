package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import java.util.HashMap;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class TransferProfile {

	private final HashMap<Stop, TransferTimeFunction> functions;

	public TransferProfile() {
		super();
		functions = new HashMap<>();
	}

	public TransferTimeFunction from(Stop start) {
		return functions.getOrDefault(start, new TransferTimeFunction());
	}

	public void updateTransfer(Connection connection, Time arrivalAtTarget) {
		Stop start = connection.start();
		TransferTimeFunction functionWithTransfer = from(start);
		Time departure = start.subtractChangeTimeFrom(connection.departure());
		TransferEntry transferEntry = new TransferEntry(departure, arrivalAtTarget);
		functionWithTransfer.update(transferEntry);
		updateNeighbours(connection, arrivalAtTarget, start);
		update(start, functionWithTransfer);
	}

	private void updateNeighbours(Connection connection, Time arrivalAtTarget, Stop start) {
		for (Stop neighbour : start.neighbours()) {
			TransferTimeFunction neighbourFunction = from(neighbour);
			Optional<RelativeTime> walkTimeTo = start.neighbours().walkTimeTo(neighbour);
			walkTimeTo
					.map(time -> connection.departure().minus(time))
					.map(time -> new TransferEntry(time, arrivalAtTarget))
					.ifPresent(entry -> neighbourFunction.update(entry));
			update(neighbour, neighbourFunction);
		}
	}

	private void update(Stop stop, TransferTimeFunction function) {
		functions.put(stop, function);
	}

	@Override
	public String toString() {
		return "TransferProfile [functions=" + functions + "]";
	}
	
}
