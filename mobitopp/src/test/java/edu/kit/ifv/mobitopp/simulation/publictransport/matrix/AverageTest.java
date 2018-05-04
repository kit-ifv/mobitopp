package edu.kit.ifv.mobitopp.simulation.publictransport.matrix;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class AverageTest {

	private static final RelativeTime shortDuration = RelativeTime.ofMinutes(1);
	private static final RelativeTime longDuration = RelativeTime.ofMinutes(5);
	private static final Time firstDeparture = SimpleTime.ofSeconds(0);
	private static final Time secondDeparture = firstDeparture.plus(shortDuration);
	private static final Time firstArrival = firstDeparture.plusMinutes(1);
	private static final Time secondArrival = secondDeparture.plus(longDuration);
	
	private ArrivalTimeSupplier arrival;
	private Average average;

	@Before
	public void initialise() {
		arrival = mock(ArrivalTimeSupplier.class);
		when(arrival.startingAt(any())).thenReturn(SimpleTime.future);
		
		average = new Average(arrival);
	}
	
	@Test
	public void travelTimeInHour() {
		when(arrival.startingAt(firstDeparture)).thenReturn(firstArrival);
		when(arrival.startingAt(secondDeparture)).thenReturn(secondArrival);

		Duration travelTime = average.inHourAfter(firstDeparture);

		Duration averageDuration = shortDuration.plus(longDuration).toDuration().dividedBy(2);
		assertThat(travelTime, is(equalTo(averageDuration)));
	}
	
	@Test
	public void travelTimeWithoutArrivals() {
		Duration travelTime = average.inHourAfter(firstDeparture);
		
		assertThat(travelTime, is(equalTo(Matrix.infinite)));
	}
}
