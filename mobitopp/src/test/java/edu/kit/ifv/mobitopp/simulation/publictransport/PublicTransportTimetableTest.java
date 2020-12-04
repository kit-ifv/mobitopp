package edu.kit.ifv.mobitopp.simulation.publictransport;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.serializer.Serializer;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.ModifiableJourneys;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFinder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Stations;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StopPoints;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicles;
import nl.jqno.equalsverifier.EqualsVerifier;

public class PublicTransportTimetableTest {

	private Connections connections;
	private StopPoints stopPoints;
	private StationFinder stationFinder;
	private Stations stations;
	private Serializer serializer;
	private ModifiableJourneys journeys;
	private Vehicles vehicles;

	@Before
	public void initialise() throws Exception {
		connections = mock(Connections.class);
		stopPoints = mock(StopPoints.class);
		journeys = mock(ModifiableJourneys.class);
		stationFinder = mock(StationFinder.class);
		stations = mock(Stations.class);
		vehicles = mock(Vehicles.class);
		serializer = mock(Serializer.class);
	}

	@Test
	public void serializesStopPoints() throws Exception {
		PublicTransportTimetable publicTransport = newPublicTransport();

		publicTransport.serializeTo(serializer);

		verify(stopPoints).serializeTo(serializer);
	}

	@Test
	public void serializesConnections() throws Exception {
		PublicTransportTimetable publicTransport = newPublicTransport();

		publicTransport.serializeTo(serializer);

		verify(connections).apply(serializer);
	}

	@Test
	public void serializesJourneys() throws Exception {
		PublicTransportTimetable publicTransport = newPublicTransport();

		publicTransport.serializeTo(serializer);

		verify(journeys).serializeJourneysTo(serializer);
	}

	private PublicTransportTimetable newPublicTransport() {
		return new PublicTransportTimetable(connections, stopPoints, journeys, stationFinder, stations, vehicles);
	}
	
	@Test
	public void delegatesVehicleScaling() throws Exception {
		PublicTransportTimetable publicTransport = newPublicTransport();

		publicTransport.vehicleScaler();
		
		verify(journeys).vehicleScaler();
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		Journey oneJourney = journey().withId(0).build();
		Journey anotherJourney = journey().withId(1).build();
		Connection oneConnection = connection().startsAt(someStop()).build();
		Connection anotherConnection = connection().startsAt(anotherStop()).build();
		EqualsVerifier
				.forClass(PublicTransportTimetable.class)
				.withPrefabValues(Point2D.class, coordinate(0, 0), coordinate(1, 1))
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.withPrefabValues(Journey.class, oneJourney, anotherJourney)
				.withPrefabValues(Connection.class, oneConnection, anotherConnection)
				.withIgnoredFields("stationFinder", "stations", "vehicles")
				.usingGetClass()
				.verify();
	}
}
