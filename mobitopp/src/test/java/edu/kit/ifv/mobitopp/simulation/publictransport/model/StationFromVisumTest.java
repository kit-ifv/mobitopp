package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.matcher.StopMatcher.hasNeighbour;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNode;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import nl.jqno.equalsverifier.EqualsVerifier;

public class StationFromVisumTest {

	private static final RelativeTime defaultWalkTime = RelativeTime.of(0, MINUTES);
	private static final Optional<RelativeTime> defaultWalkTimeAsOptional = Optional
			.of(defaultWalkTime);
	private TransferWalkTime transitionWalkTime;

	@Before
	public void initialise() throws Exception {
		transitionWalkTime = mock(TransferWalkTime.class);
	}

	@Test
	public void addsAlreadyAddedStopAsNeighbourWhenARelationExists() throws Exception {
		when(transitionWalkTime.walkTime(any(), any())).thenReturn(defaultWalkTimeAsOptional);
		Stop neighbour = stop().withId(1).withName("neighbour").build();
		Stop stop = stop().withId(2).withName("stop").build();
		StationFromVisum station = someStation();

		station.add(neighbour);
		station.add(stop);

		List<Stop> stops = new ArrayList<>();
		station.forEach(stops::add);
		assertThat(stops, containsInAnyOrder(neighbour, stop));
		verify(transitionWalkTime).walkTime(neighbour, stop);
		verify(transitionWalkTime).walkTime(stop, neighbour);
		assertThat(stop, hasNeighbour(neighbour));
		assertThat(neighbour, hasNeighbour(stop));
	}

	@Test
	public void resolveMinimumChangeTime() throws Exception {
		int stop = 1;
		when(transitionWalkTime.minimumChangeTime(stop)).thenReturn(defaultWalkTime);
		StationFromVisum station = someStation();

		RelativeTime minimumChangeTime = station.minimumChangeTime(stop);

		assertThat(minimumChangeTime, is(equalTo(defaultWalkTime)));
		verify(transitionWalkTime).minimumChangeTime(stop);
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		VisumNode some = visumNode().withId(1).build();
		VisumNode other = visumNode().withId(2).build();
		EqualsVerifier
				.forClass(StationFromVisum.class)
				.usingGetClass()
				.withPrefabValues(VisumNode.class, some, other)
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.withOnlyTheseFields("id")
				.verify();
	}

	private StationFromVisum someStation() {
		return newStation(0);
	}

	private StationFromVisum newStation(int id) {
		return new StationFromVisum(id, transitionWalkTime, emptyList());
	}

}
