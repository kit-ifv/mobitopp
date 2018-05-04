package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class RouteIncludingFootpathsTest {

	private static final RelativeTime oneMinute = RelativeTime.of(1, MINUTES);
	private static final RelativeTime twoMinutes = RelativeTime.of(2, MINUTES);

	private PublicTransportRoute route;

	@Before
	public void initialise() throws Exception {
		route = mock(PublicTransportRoute.class);
	}
	
	@Test
	public void extendsEndDateByWalkTimeToEnd() throws Exception {
		when(route.arrival()).thenReturn(someTime());
		StopPath start = someDistance();
		StopPath end = anotherDistance();
		PublicTransportRoute routeIncludingFootpath = new RouteIncludingFootpaths(route, start, end);

		Time endDate = routeIncludingFootpath.arrival();

		Time endTimeIncludingWalk = someTime().plus(twoMinutes);
		assertThat(endDate, is(equalTo(endTimeIncludingWalk)));
	}

	@Test
	public void expandsDurationWithFootpaths() throws Exception {
		when(route.duration()).thenReturn(RelativeTime.of(3, MINUTES));
		StopPath start = someDistance();
		StopPath end = anotherDistance();
		RouteIncludingFootpaths completeTour = new RouteIncludingFootpaths(route, start, end);

		RelativeTime duration = completeTour.duration();
		
		assertThat(duration, is(equalTo(RelativeTime.of(6, MINUTES))));
	}

	private StopPath someDistance() {
		return new StopPath(someStop(), oneMinute);
	}

	private StopPath anotherDistance() {
		return new StopPath(anotherStop(), twoMinutes);
	}
	
	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(RouteIncludingFootpaths.class)
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.usingGetClass()
				.verify();
	}
}
