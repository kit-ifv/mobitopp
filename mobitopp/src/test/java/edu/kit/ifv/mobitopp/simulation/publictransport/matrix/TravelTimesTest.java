package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static edu.kit.ifv.mobitopp.simulation.publictransport.matrix.Matrix.infinite;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;

public class TravelTimesTest {

	private static final int wholeDay = 24;
	private static final int someHour = 0;
	private static final int oneHourLater = 1;
	private static final int someZoneId = 1;
	private static final int anotherZoneId = 2;
	private Zone someZone;
	private Zone anotherZone;
	private TravelTimes travelTimes;

	@Before
	public void initialise() throws Exception {
		someZone = mock(Zone.class);
		anotherZone = mock(Zone.class);
		List<Integer> oids = asList(someZoneId, anotherZoneId);

		when(someZone.getOid()).thenReturn(someZoneId);
		when(anotherZone.getOid()).thenReturn(anotherZoneId);
		travelTimes = new TravelTimes(oids, wholeDay);
	}

	@Test
	public void isInfiniteForUnavailableOriginDestinationPair() throws Exception {
		assertThat(travelTimes.time(someHour, someZone, anotherZone), is(infinite));
	}

	@Test
	public void isUpdatedValueForGivenOriginDestinationPair() throws Exception {
		Duration travelTime = Duration.of(1, MINUTES);

		travelTimes.update(someHour, someZone, anotherZone, travelTime);

		assertThat(travelTimes.time(someHour, someZone, anotherZone), is(travelTime));
	}

	@Test
	public void isInfiniteForOriginDestinationPairInLaterHour() throws Exception {
		travelTimes.update(someHour, someZone, anotherZone, Duration.of(1, MINUTES));

		assertThat(travelTimes.time(oneHourLater, someZone, anotherZone), is(infinite));
	}

	@Test
	public void isInfiniteForOriginDestinationPairInEarlierHour() throws Exception {
		travelTimes.update(oneHourLater, someZone, anotherZone, Duration.of(1, MINUTES));

		assertThat(travelTimes.time(someHour, someZone, anotherZone), is(infinite));
	}

	@Test
	public void fixInnerZoneTime() {
		Duration innerZoneTime = Duration.of(1, MINUTES);
		float someSeconds = innerZoneTime.toMinutes();
		travelTimes.fixInnerZoneTime(someZone, someSeconds);
		
		Duration inSomeHour = travelTimes.time(someHour, someZone, someZone);
		Duration inAnotherHour = travelTimes.time(oneHourLater, someZone, someZone);
		
		assertThat(inSomeHour, is(equalTo(innerZoneTime)));
		assertThat(inAnotherHour, is(equalTo(innerZoneTime)));
	}

}
