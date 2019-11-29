package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFinder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationPaths;
import edu.kit.ifv.mobitopp.time.Time;

public class PublicTransportImpedanceTest {

	private static final Location target = new Example().location(coordinate(1, 1));
	private static final Location source = new Example().location();
	private static final Location sameAsSource = new Example().location();
	private static final Mode notPublicTransport = StandardMode.BIKE;
	private RouteSearch routeSearch;
	private ImpedanceIfc impedance;
	private Time date;
	private StationFinder stationResolver;

	@Before
	public void initialise() {
		routeSearch = mock(RouteSearch.class);
		impedance = mock(ImpedanceIfc.class);
		stationResolver = mock(StationFinder.class);
		date = Data.someTime();
	}

	@Test
	public void delegateToImpedanceWhenModeIsNotPublicTransport() throws Exception {
		PublicTransportImpedance publicTransport = publicTransport();
		publicTransport.getPublicTransportRoute(source, target, notPublicTransport, date);

		verify(impedance).getPublicTransportRoute(source, target, notPublicTransport, date);
		verifyNoMoreInteractions(impedance);
		verifyZeroInteractions(routeSearch);
	}

	@Test
	public void searchesNearestsStationBeforeCalculatingTour() throws Exception {
		Time time = date;
		StationPaths startStations = mock(StationPaths.class);
		StationPaths endStations = mock(StationPaths.class);
		when(stationResolver.findReachableStations(source)).thenReturn(startStations);
		when(stationResolver.findReachableStations(target)).thenReturn(endStations);
		PublicTransportRoute expectedTour = mock(PublicTransportRoute.class);
		when(startStations.findRoute(endStations, time, routeSearch)).thenReturn(Optional.of(expectedTour));

		PublicTransportImpedance publicTransport = publicTransport();
		Optional<PublicTransportRoute> tour = publicTransport.getPublicTransportRoute(source, target,
				StandardMode.PUBLICTRANSPORT, date);

		assertThat("find shortest tour", tour, hasValue(expectedTour));
		verify(stationResolver).findReachableStations(source);
		verify(stationResolver).findReachableStations(target);
		verify(startStations).findRoute(endStations, time, routeSearch);
		verifyZeroInteractions(routeSearch);
		verifyZeroInteractions(impedance);
	}

	@Test
	public void doNotSearchWhenSourceAndTargetLocationAreEqual() throws Exception {
		Optional<PublicTransportRoute> route = publicTransport().getPublicTransportRoute(source,
				sameAsSource, StandardMode.PUBLICTRANSPORT, date);

		assertThat(route, isEmpty());

		verifyZeroInteractions(stationResolver);
		verifyZeroInteractions(impedance);
	}

	private PublicTransportImpedance publicTransport() {
		PublicTransportImpedance publicTransport = new PublicTransportImpedance(routeSearch, stationResolver,
				impedance);
		return publicTransport;
	}

}
