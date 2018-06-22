package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.fromSomeToAnother;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.laterFromAnotherToOther;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ConnectionScanTest {

	private ConnectionSweeper connections;
	private PreparedSearchRequest searchRequest;
	private TransitNetwork transitNetwork;
	private ConnectionScan scan;
	private Time searchTime;
	private Stop start;
	private Stop end;
	private StopPaths starts;
	private StopPaths ends;

	@Before
	public void initialise() throws Exception {
		transitNetwork = mock(TransitNetwork.class);
		connections = mock(ConnectionSweeper.class);
		searchRequest = mock(PreparedSearchRequest.class);
		searchTime = someTime();
		start = someStop();
		end = otherStop();
		starts = mock(StopPaths.class);
		ends = mock(StopPaths.class);

		when(transitNetwork.connections()).thenReturn(connections);
		scan = scan(transitNetwork);
	}

	private ConnectionScan scan(TransitNetwork timetable) {
		return new ConnectionScan(timetable) {

			@Override
			PreparedSearchRequest newSearchRequest(StopPaths fromStarts, StopPaths toEnds, Time atTime) {
				return searchRequest;
			}
		};
	}

	@Test
	public void scanNotNeededFromStopToStop() throws Exception {
		scanNotNeeded(start, end, searchTime);

		Optional<PublicTransportRoute> route = scan.findRoute(start, end, searchTime);

		assertThat(route, isEmpty());
		verifyScanNotNeededBetweenSingleStops();
		verifyNoSweep();
	}

	private void scanNotNeeded(Stop start, Stop end, Time atTime) {
		when(transitNetwork.scanNotNeeded(start, end, atTime)).thenReturn(true);
	}

	private void verifyScanNotNeededBetweenSingleStops() {
		verify(transitNetwork).scanNotNeeded(start, end, searchTime);
	}

	@Test
	public void fromOneStopToAnotherStop() throws Exception {
		List<Stop> stops = asList(start, end);
		use(stops);

		PublicTransportRoute sweepedRoute = mock(PublicTransportRoute.class);
		scanNeeded(start, end, searchTime);
		when(connections.sweep(any())).thenReturn(of(sweepedRoute));

		Optional<PublicTransportRoute> route = scan.findRoute(start, end, searchTime);

		assertThat(route, isPresent());
		assertThat(route, hasValue(equalTo(sweepedRoute)));
		verify(connections).sweep(any());
	}

	private void use(Collection<Stop> stops) {
		when(transitNetwork.stops()).thenReturn(stops);
	}

	private void scanNeeded(Stop start, Stop end, Time atTime) {
		when(transitNetwork.scanNotNeeded(start, end, atTime)).thenReturn(false);
	}

	private void verifyNoSweep() {
		verifyZeroInteractions(connections);
	}

	@Test
	public void findsRouteBetweenSeveralStops() throws Exception {
		PublicTransportRoute sweepedRoute = mock(PublicTransportRoute.class);
		scanNeeded(starts, ends, searchTime);
		when(connections.sweep(searchRequest)).thenReturn(of(sweepedRoute));

		Optional<PublicTransportRoute> route = scan.findRoute(starts, ends, searchTime);

		assertThat(route, isPresent());
		assertThat(route, hasValue(sweepedRoute));
		verify(connections).sweep(searchRequest);
	}

	private void scanNeeded(StopPaths starts, StopPaths ends, Time searchTime) {
		when(transitNetwork.scanNotNeeded(starts, ends, searchTime)).thenReturn(false);
	}

	@Test
	public void scanNotNeededBetweenSeveralStops() throws Exception {
		scanNotNeeded(starts, ends, searchTime);

		Optional<PublicTransportRoute> route = scan.findRoute(starts, ends, searchTime);

		assertThat(route, isEmpty());
		verifyScanNotNeededBetweenSeveralStops();
		verifyNoSweep();
	}

	private void verifyScanNotNeededBetweenSeveralStops() {
		verify(transitNetwork).scanNotNeeded(starts, ends, searchTime);
	}

	private void scanNotNeeded(StopPaths starts, StopPaths ends, Time time) {
		when(transitNetwork.scanNotNeeded(starts, ends, time)).thenReturn(true);
	}
	
	@Test
	public void considersPathToDestination() {
		ConnectionScan connectionScan = new ConnectionScan(simpleNetwork());
		StopPaths fromStart = DefaultStopPaths.from(asList(originPath()));
		StopPaths toEnd = DefaultStopPaths.from(asList(longPath(), shortPath()));
		
		Optional<PublicTransportRoute> route = connectionScan.findRoute(fromStart, toEnd, searchTime);
		
		assertThat(route, hasValue(routeViaOtherStop()));
	}

	protected TransitNetwork simpleNetwork() {
		Collection<Stop> stops = asList(someStop(), anotherStop(), otherStop());
		Connections connections = new Connections();
		connections.add(Data.fromSomeToAnother());
		connections.add(Data.laterFromAnotherToOther());
		return TransitNetwork.createOf(stops, connections);
	}

	private PublicTransportRoute routeViaOtherStop() {
		Time arrival = laterFromAnotherToOther().arrival();
		List<Connection> connections = asList(fromSomeToAnother(), laterFromAnotherToOther());
		PublicTransportRoute route = new ScannedRoute(someStop(), otherStop(), searchTime, arrival, connections);
		return new RouteIncludingFootpaths(route, originPath(), shortPath());
	}

	private StopPath longPath() {
		return new StopPath(anotherStop(), RelativeTime.ofMinutes(10));
	}

	private StopPath shortPath() {
		return new StopPath(otherStop(), RelativeTime.ofMinutes(1));
	}

	private StopPath originPath() {
		return new StopPath(someStop(), RelativeTime.ZERO);
	}

}
