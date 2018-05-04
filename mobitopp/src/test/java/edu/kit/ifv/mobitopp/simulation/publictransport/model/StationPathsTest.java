package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.simulation.publictransport.StationBuilder.station;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.DefaultStopPaths;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.StopPaths;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class StationPathsTest {

	private static final RelativeTime threeMinutes = RelativeTime.of(3, MINUTES);
	private static final RelativeTime someDuration = RelativeTime.of(10, MINUTES);
	private RouteSearch viaRouteSearch;
	private PublicTransportRoute someTour;
	private StopPaths someStops;
	private StopPaths anotherStops;
	private Stop anotherStop;
	private Station someStation;
	private Station anotherStation;
	private Station severalStopsStation;
	private StopPath someStopDistance;
	private Stop someStop;
	private StopPath anotherStopDistance;
	private StopPaths severalStops;

	@Before
	public void initialise() throws Exception {
		viaRouteSearch = mock(RouteSearch.class);
		someTour = mock(PublicTransportRoute.class);
		when(someTour.duration()).thenReturn(someDuration);

		someStation = station().with(1).build();
		someStop = stop().withId(1).build();
		someStation.add(someStop);
		someStopDistance = new StopPath(someStop, RelativeTime.of(1, MINUTES));
		someStops = DefaultStopPaths.from(asList(someStopDistance));
		anotherStation = station().with(2).build();
		anotherStop = stop().withId(2).build();
		anotherStation.add(anotherStop);
		anotherStopDistance = new StopPath(anotherStop, RelativeTime.of(2, MINUTES));
		anotherStops = DefaultStopPaths.from(asList(anotherStopDistance));
		severalStopsStation = station().with(3).build();
		Stop firstOfSeveral = stop().withId(3).build();
		Stop secondOfSeveral = stop().withId(4).build();
		severalStopsStation.add(firstOfSeveral);
		severalStopsStation.add(secondOfSeveral);
		StopPath firstDistance = new StopPath(firstOfSeveral, threeMinutes);
		StopPath secondsDistance = new StopPath(secondOfSeveral, threeMinutes);
		severalStops = DefaultStopPaths.from(asList(firstDistance, secondsDistance));
	}

	@Test
	public void travelsNoWhereWithoutStartStations() throws Exception {
		StationPaths toEnd = new StationPaths(singleDistance());

		StationPaths start = new StationPaths(noDistance());
		Optional<PublicTransportRoute> tour = start.findRoute(toEnd, atSomeTime(), viaRouteSearch);

		verifyZeroInteractions(viaRouteSearch);
		assertThat(tour, isEmpty());
	}

	@Test
	public void travelsNoWhereWithoutEndStations() throws Exception {
		StationPaths toEnd = new StationPaths(noDistance());

		StationPaths resolved = new StationPaths(singleDistance());
		Optional<PublicTransportRoute> tour = resolved.findRoute(toEnd, atSomeTime(), viaRouteSearch);

		verifyZeroInteractions(viaRouteSearch);
		assertThat(tour, isEmpty());
	}

	@Test
	public void travelsFromOneStationToOneOtherStation() throws Exception {
		List<StationPath> endStations = asList(anotherDistance());
		StationPaths toEnd = new StationPaths(endStations);

		List<StationPath> startStations = asList(someDistance());
		when(viaRouteSearch.findRoute(someStops, anotherStops, atSomeTime()))
				.thenReturn(Optional.of(someTour));

		StationPaths resolved = new StationPaths(startStations);
		Optional<PublicTransportRoute> tour = resolved.findRoute(toEnd, atSomeTime(), viaRouteSearch);

		assertThat(tour, isPresent());
		assertThat(tour.get().duration(), is(equalTo(someDuration)));
		verify(viaRouteSearch).findRoute(someStops, anotherStops, atSomeTime());
	}

	@Test
	public void travelsFromOneStationToOneOtherStationWithSeveralStops() throws Exception {
		List<StationPath> startStations = asList(someDistance());
		List<StationPath> endStations = asList(otherDistance());
		StationPaths toEnd = new StationPaths(endStations);

		when(viaRouteSearch.findRoute(someStops, severalStops, atSomeTime()))
				.thenReturn(Optional.of(someTour));

		StationPaths resolved = new StationPaths(startStations);
		Optional<PublicTransportRoute> tour = resolved.findRoute(toEnd, atSomeTime(), viaRouteSearch);

		assertThat(tour, isPresent());
		assertThat(tour.get().duration(), is(equalTo(someDuration)));
		verify(viaRouteSearch).findRoute(someStops, severalStops, atSomeTime());
	}

	@Test
	public void travelsFromOneStationWithSeveralStopsToOneOtherStation() throws Exception {
		List<StationPath> endStations = asList(someDistance());
		StationPaths toEnd = new StationPaths(endStations);

		List<StationPath> startStations = asList(otherDistance());
		when(viaRouteSearch.findRoute(severalStops, someStops, atSomeTime()))
				.thenReturn(Optional.of(someTour));

		StationPaths resolved = new StationPaths(startStations);
		Optional<PublicTransportRoute> tour = resolved.findRoute(toEnd, atSomeTime(), viaRouteSearch);

		assertThat(tour, isPresent());
		assertThat(tour.get().duration(), is(equalTo(someDuration)));
		verify(viaRouteSearch).findRoute(severalStops, someStops, atSomeTime());
	}

	private static List<StationPath> noDistance() {
		return emptyList();
	}

	private List<StationPath> singleDistance() {
		return singletonList(someDistance());
	}

	private StationPath someDistance() {
		return new StationPath(someStation, someDuration());
	}

	private StationPath anotherDistance() {
		return new StationPath(anotherStation, anotherDuration());
	}

	private StationPath otherDistance() {
		return new StationPath(severalStopsStation, otherDuration());
	}

	private static RelativeTime someDuration() {
		return RelativeTime.of(1, MINUTES);
	}

	private static RelativeTime anotherDuration() {
		return RelativeTime.of(2, MINUTES);
	}

	private static RelativeTime otherDuration() {
		return threeMinutes;
	}

	private static Time atSomeTime() {
		return Data.someTime();
	}

}
