package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.oneMinuteLater;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumStop;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumStopArea;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumStopPoint;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.serializer.StationResolver;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPtStop;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;

public class VisumPublicTransportFactoryTest {

	private VisumPublicTransportFactory factory;
	private Journey journey;
	private StationResolver stationResolver;
	private VisumNode someNode;
	private Station mockedStation;

	@Before
	public void initialise() throws Exception {
		journey = mock(Journey.class);
		stationResolver = mock(StationResolver.class);
		mockedStation = mock(Station.class);
		factory = new VisumPublicTransportFactory(stationResolver);
	}

	@Test
	public void assignsUniqueIdsForEachConnection() throws Exception {
		Connection firstConnection = createConnection();
		Connection secondConnection = createConnection();

		assertThat(firstConnection.id(), is(0));
		assertThat(secondConnection.id(), is(1));
	}

	private Connection createConnection() {
		RoutePoints route = RoutePoints.from(someStop(), anotherStop());
		return factory.connectionFrom(someStop(), anotherStop(), someTime(), oneMinuteLater(), journey,
				route);
	}

	@Test
	public void createsStopFromVisum() throws Exception {
		Stop stop = factory.stopFrom(singleStop());

		assertThat(stop.externalId(), is(1));
		assertThat(stop.id(), is(0));
		assertThat(stop.coordinate(), is(new Point2D.Double(0, 0)));
		assertThat(stop.name(), is("name"));

		verify(stationResolver).getStation(1);
	}

	@Test
	public void assignsUniqueInternalIdToEachStop() throws Exception {
		Stop first = factory.stopFrom(singleStop());
		Stop second = factory.stopFrom(singleStop());

		assertThat(first.id(), is(0));
		assertThat(second.id(), is(1));
	}

	private VisumPtStopPoint singleStop() {
		int firstStationId = 1;
		Station firstStation = mock(Station.class);
		when(firstStation.minimumChangeTime(anyInt())).thenReturn(RelativeTime.of(1, MINUTES));
		when(stationResolver.getStation(firstStationId)).thenReturn(firstStation);
		VisumPtStop firstStop = visumStop().withId(firstStationId).build();
		int firstAreaId = 1;
		VisumPtStopArea firstArea = visumStopArea().withId(firstAreaId).with(firstStop).build();
		int firstId = 1;
		Point2D coordinate = new Point2D.Double();
		return visumStopPoint().withId(firstId).with(firstArea).named("name").at(coordinate).build();
	}

	@Test
	public void addStopToStation() throws Exception {
		when(stationResolver.getStation(1)).thenReturn(mockedStation);

		Stop stop = factory.stopFrom(stopPointWithNode());
		
		assertThat(stop.station(), is(equalTo(mockedStation)));

		verify(mockedStation).add(stop);
	}

	private VisumPtStopPoint stopPointWithNode() {
		int stationId = 1;
		VisumPtStop stop = visumStop().withId(stationId).build();
		VisumPtStopArea stopArea = visumStopArea().with(someNode).with(stop).build();
		return visumStopPoint().with(stopArea).build();
	}
}
