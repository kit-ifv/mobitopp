package edu.kit.ifv.mobitopp.simulation.publictransport;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.yetAnotherStop;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.serializer.JourneyProvider;

@ExtendWith(MockitoExtension.class)
class ConnectionsVerifierTest {

	@Mock
	private PublicTransportTimetable timetable;
	@Mock
	private JourneyProvider journeyProvider;
	@Mock
	private ModifiableJourney journey;

	@Test
	void interruptedConnectionsAreDetected() {
		Connections connections = interruptedConnections();

		when(timetable.createJourneyProvider()).thenReturn(journeyProvider);
		when(journeyProvider.stream()).thenReturn(Stream.of(journey));
		when(journey.connections()).thenReturn(connections);
		TimetableVerifier verifier = new ConnectionsVerifier();

		assertThrows(IOException.class, () -> verifier.verify(timetable));
	}

	@Test
	void consecutiveConnectionsAreSane() throws IOException {
		Connections connections = consecutiveConnections();

		when(timetable.createJourneyProvider()).thenReturn(journeyProvider);
		when(journeyProvider.stream()).thenReturn(Stream.of(journey));
		when(journey.connections()).thenReturn(connections);
		TimetableVerifier verifier = new ConnectionsVerifier();

		assertDoesNotThrow(() -> verifier.verify(timetable));
	}

	private Connections interruptedConnections() {
		Connections connections = new Connections();
		connections.add(connection().startsAt(someStop()).endsAt(anotherStop()).build());
		connections.add(connection().startsAt(yetAnotherStop()).endsAt(otherStop()).build());
		return connections;
	}

	private Connections consecutiveConnections() {
		Connections connections = new Connections();
		connections.add(connection().startsAt(someStop()).endsAt(anotherStop()).build());
		connections.add(connection().startsAt(anotherStop()).endsAt(otherStop()).build());
		return connections;
	}

}
