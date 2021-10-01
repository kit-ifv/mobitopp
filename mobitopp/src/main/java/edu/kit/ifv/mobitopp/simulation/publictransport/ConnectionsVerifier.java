package edu.kit.ifv.mobitopp.simulation.publictransport;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;

public class ConnectionsVerifier implements TimetableVerifier {

	@Override
	public void verify(PublicTransportTimetable timetable) throws IOException {
		List<ModifiableJourney> interruptedJourneys = timetable
			.createJourneyProvider()
			.stream()
			.filter(this::interruptedJourneys)
			.collect(toList());
		if (0 != interruptedJourneys.size()) {
			String interruptedIds = interruptedJourneys
				.stream()
				.map(ModifiableJourney::id)
				.map(String::valueOf)
				.collect(joining(" ,"));
			throw new IOException(
				"Connections are not consecutive for journeys: " + interruptedIds);
		}
	}

	private boolean interruptedJourneys(ModifiableJourney journey) {
		Collection<Connection> connections = journey.connections().asCollection();
		Connection lastConnection = null;
		for (Connection connection : connections) {
			if (null != lastConnection && !lastConnection.end().equals(connection.start())) {
				return true;
			}
			lastConnection = connection;
		}
		return false;
	}

}
