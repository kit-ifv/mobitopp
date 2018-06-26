package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import java.util.Collection;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class TimetableToVehicleConverter extends BaseVehicleConverter
		implements VehicleTimesConverter {

	public static VehicleTimes from(Collection<Connection> connections) {
		VehicleTimesConverter converter = new TimetableToVehicleConverter();
		return converter.convert(connections);
	}

	protected TravelTime travelTimeOf(Connection current) {
		return new TravelTime(current.id(), current.duration());
	}

	protected WaitTime waitingBetween(Connection previous, Connection current) {
		RelativeTime waitTime = current.departure().differenceTo(previous.arrival());
		return new WaitTime(waitTime);
	}

}
