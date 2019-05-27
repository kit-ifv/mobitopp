package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static edu.kit.ifv.mobitopp.publictransport.matcher.StopMatcher.hasNeighbour;
import static edu.kit.ifv.mobitopp.publictransport.matcher.StopMatcher.hasNoNeighbour;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper.asSet;
import static edu.kit.ifv.mobitopp.simulation.publictransport.VisumWalkLinks.minimumTime;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumLink;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNode;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumStop;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumStopArea;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumStopPoint;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.serializer.NeighbourhoodCoupler;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumLinkType;
import edu.kit.ifv.mobitopp.visum.VisumLinks;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPtStop;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;

public class VisumWalkLinksTest {

	private ShortestPathSearch shortestPathSearch;

	@After
	public void clearTransportSystems()
			throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		ReflectionHelper.clearTransportSystemSetCache();
	}

	@Before
	public void initialise() throws Exception {
		shortestPathSearch = mock(ShortestPathSearch.class);
	}

	private void searchDoesNotFindAnything() {
		ShortestPathsToStations result = mock(ShortestPathsToStations.class);
		when(result.durationTo(any())).thenReturn(Optional.empty());
		when(shortestPathSearch.search(any(VisumNode.class), any())).thenReturn(result);
	}

	@Test
	public void addsNothingToStopWhenNoLinksAreAvailable() throws Exception {
		NeighbourhoodCoupler walkLinks = from(emptyLinks(), emptyStopAreas(), emptyList());
		walkLinks.addNeighboursshipBetween(someStop(), anotherStop());

		assertThat(someStop(), hasNoNeighbour());
		assertThat(anotherStop(), hasNoNeighbour());
		verifyZeroInteractions(shortestPathSearch);
	}

	private static Map<Integer, VisumPtStopArea> emptyStopAreas() {
		return Collections.emptyMap();
	}

	private static VisumLinks emptyLinks() {
		return new VisumLinks(Collections.emptyMap());
	}

	@Test
	public void addsOneStopToNeighbourhoodWhenWalkLinkIsAvailable() throws Exception {
		searchDoesNotFindAnything();
		Stop someStop = someStop();
		Stop anotherStop = anotherStop();

		VisumLinks links = singleMatchingLinks();
		Map<Integer, VisumPtStopArea> stopAreas = twoStopAreas();
		Collection<VisumPtStopPoint> stopPoints = twoStopPoints();
		NeighbourhoodCoupler walkLinks = from(links, stopAreas, stopPoints);
		walkLinks.addNeighboursshipBetween(someStop, anotherStop);

		assertThat("start to end", someStop, hasNeighbour(anotherStop));
		assertThat("end to start", anotherStop, hasNeighbour(someStop));
		verify(shortestPathSearch, times(2)).search(any(VisumNode.class), any());
	}

	private NeighbourhoodCoupler from(
			VisumLinks links, Map<Integer, VisumPtStopArea> stopAreas,
			Collection<VisumPtStopPoint> stopPoints) {
		return VisumWalkLinks.from(links, stopAreas, stopPoints, shortestPathSearch,
				transferWalkType());
	}

	private static Map<Integer, VisumPtStopArea> twoStopAreas() {
		HashMap<Integer, VisumPtStopArea> areas = new HashMap<>();
		areas.put(1, firstStopArea());
		areas.put(2, secondStopArea());
		return areas;
	}

	private Collection<VisumPtStopPoint> twoStopPoints() {
		VisumPtStopPoint first = visumStopPoint().withId(1).with(firstStopArea()).build();
		VisumPtStopPoint second = visumStopPoint().withId(2).with(secondStopArea()).build();
		return asList(first, second);
	}

	@Test
	public void usesShortestPathSearchToFindAdditionalWalkTimes() throws Exception {
		ShortestPathsToStations result = mock(ShortestPathsToStations.class);
		when(result.durationTo(any())).thenReturn(Optional.of(RelativeTime.of(1, MINUTES)));
		when(shortestPathSearch.search(any(VisumNode.class), any())).thenReturn(result);
		Stop someStop = someStop();
		Stop anotherStop = anotherStop();
		VisumLinks notNeededLinks = emptyLinks();

		NeighbourhoodCoupler walkLinks = from(notNeededLinks, twoStopAreas(), twoStopPoints());
		walkLinks.addNeighboursshipBetween(someStop, anotherStop);

		verify(shortestPathSearch, times(2)).search(any(VisumNode.class), any());
		assertThat("start to end", someStop, hasNeighbour(anotherStop));
		assertThat("end to start", anotherStop, hasNeighbour(someStop));
	}

	@Test
	public void overridesSlowerWalkTimeByShortestPathSearchEvenWhenStopAreasBelongToSameVisumStop()
			throws Exception {
		Stop someStop = someStop();
		Stop anotherStop = anotherStop();
		VisumLinks longLink = singleMatchingLinks();
		RelativeTime shorterTime = RelativeTime.of(1, MINUTES);

		ShortestPathsToStations pathToStations = mock(ShortestPathsToStations.class);
		when(pathToStations.durationTo(any())).thenReturn(Optional.of(shorterTime));
		when(shortestPathSearch.search(any(VisumNode.class), anyList())).thenReturn(pathToStations);

		NeighbourhoodCoupler walkLinks = from(longLink, areasOfSameVisumStop(), twoStopPoints());
		walkLinks.addNeighboursshipBetween(someStop, anotherStop);

		verify(shortestPathSearch, times(2)).search(any(VisumNode.class), any());
		assertThat("start to end", someStop, hasNeighbour(anotherStop));
		assertThat("end to start", anotherStop, hasNeighbour(someStop));
		assertThat(someStop.neighbours().walkTimeTo(anotherStop), hasValue(shorterTime));
	}

	private Map<Integer, VisumPtStopArea> areasOfSameVisumStop() {
		VisumPtStop sameStop = visumStop().withId(1).build();
		VisumPtStopArea firstStopArea = visumStopArea()
				.with(firstNode())
				.withId(someStop().externalId())
				.with(sameStop)
				.build();
		VisumPtStopArea secondStopArea = visumStopArea()
				.with(secondNode())
				.withId(anotherStop().externalId())
				.with(sameStop)
				.build();
		HashMap<Integer, VisumPtStopArea> areas = new HashMap<>();
		areas.put(firstStopArea.id, firstStopArea);
		areas.put(secondStopArea.id, secondStopArea);
		return areas;
	}

	@Test
	public void useMinimumWalkTimeBetweenNeighbours() throws Exception {
		Stop someStop = someStop();
		Stop anotherStop = anotherStop();
		VisumLinks longLink = singleMatchingLinks();
		RelativeTime tooShortTime = RelativeTime.of(0, MINUTES);

		ShortestPathsToStations pathToStations = mock(ShortestPathsToStations.class);
		when(pathToStations.durationTo(any())).thenReturn(Optional.of(tooShortTime));
		when(shortestPathSearch.search(any(VisumNode.class), anyList())).thenReturn(pathToStations);

		NeighbourhoodCoupler walkLinks = from(longLink, areasOfSameVisumStop(), twoStopPoints());
		walkLinks.addNeighboursshipBetween(someStop, anotherStop);

		verify(shortestPathSearch, times(2)).search(any(VisumNode.class), any());
		assertThat("start to end", someStop, hasNeighbour(anotherStop));
		assertThat("end to start", anotherStop, hasNeighbour(someStop));
		assertThat(someStop.neighbours().walkTimeTo(anotherStop), hasValue(minimumTime));
	}

	private static VisumPtStopArea firstStopArea() {
		VisumPtStop someStop = visumStop().withId(1).build();
		return visumStopArea().with(firstNode()).withId(1).with(someStop).build();
	}

	private static VisumPtStopArea secondStopArea() {
		VisumPtStop anotherStop = visumStop().withId(2).build();
		return visumStopArea().with(secondNode()).withId(2).with(anotherStop).build();
	}

	private VisumLinks singleMatchingLinks() {
		VisumLinkType type = transferWalkType();
		VisumLink link = visumLink()
				.from(firstNode())
				.to(secondNode())
				.with(type)
				.withLength(4)
				.withWalkSpeed(4)
				.build();
		return new VisumLinks(Collections.singletonMap(1, link));
	}

	private VisumLinkType transferWalkType() {
		VisumTransportSystem system = new VisumTransportSystem("F", "Fusswege", "F");
		VisumTransportSystemSet systemSet = asSet(system);
		return new VisumLinkType(7, "�V Umsteigefu�wege", systemSet, 0, 0, 0, 0);
	}

	private static VisumNode firstNode() {
		return visumNode().withId(1).build();
	}

	private static VisumNode secondNode() {
		return visumNode().withId(2).build();
	}
}
