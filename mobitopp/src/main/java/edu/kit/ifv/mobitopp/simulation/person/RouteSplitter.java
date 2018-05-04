package edu.kit.ifv.mobitopp.simulation.person;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class RouteSplitter {

	static List<PublicTransportLeg> splitInParts(PublicTransportRoute route) {
		ArrayList<PublicTransportLeg> legs = new ArrayList<>();
		Stop start = null;
		Stop end = null;
		Time departure = null;
		Time arrival = null;
		Journey journey = null;
		List<Connection> last = new ArrayList<>();
		for (Connection connection : route.connections()) {
			if (journey == null) {
				start = connection.start();
				departure = connection.departure();
				journey = connection.journey();
			}
			if (!journey.equals(connection.journey())) {
				legs.add(newTrip(start, end, departure, arrival, journey, last));
				start = connection.start();
				departure = connection.departure();
				journey = connection.journey();
				last = new ArrayList<>();
			}
			end = connection.end();
			arrival = connection.arrival();
			last.add(connection);
		}
		if (journey != null) {
			legs.add(newTrip(start, end, departure, arrival, journey, last));
		}
		return legs;
	}

	private static PublicTransportLeg newTrip(
			Stop start, Stop end, Time departure, Time arrival, Journey journey, List<Connection> last) {
		return new PublicTransportLeg(start, end, journey, departure, arrival, last);
	}
}
