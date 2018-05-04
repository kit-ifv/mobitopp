package edu.kit.ifv.mobitopp.util;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

public class StopWatchTest {

	private Duration firstTask;
	private Duration secondTask;
	private LocalDateTime start;
	private LocalDateTime intermediate;
	private LocalDateTime end;
	private Supplier<LocalDateTime> timer;
	private StopWatch stopWatch;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		timer = mock(Supplier.class);
		firstTask = Duration.of(1, MINUTES);
		secondTask = Duration.of(2, MINUTES);
		start = LocalDateTime.of(1, 1, 1, 0, 0);
		intermediate = start.plus(firstTask);
		end = intermediate.plus(secondTask);

		stopWatch = new StopWatch(timer);
	}

	@Test
	public void measuresRuntimes() {
		when(timer.get()).thenReturn(start, intermediate, end);

		stopWatch.start();
		stopWatch.measurePoint("intermediate");
		stopWatch.measurePoint("end");

		Map<String, Duration> runtime = new HashMap<>();
		stopWatch.forEach(runtime::put);
		assertThat(runtime.get("intermediate"), is(equalTo(firstTask)));
		assertThat(runtime.get("end"), is(equalTo(secondTask)));
	}
	
	@Test(expected=IllegalStateException.class)
	public void measureBeforeStart() {
		stopWatch.measurePoint("illegal");
	}
}
