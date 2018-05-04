package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.time.Time.future;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.function.BiConsumer;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopBuilder;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class MultipleStartsTest {

	private static final int onlyStartStops = 2;
	private static final int additionalStops = 4;
	private static final RelativeTime changeTime = RelativeTime.of(1, MINUTES);
	private static final RelativeTime noMinutes = RelativeTime.ZERO;
	private static final RelativeTime oneMinute = RelativeTime.of(1, MINUTES);
	private static final RelativeTime twoMinutes = RelativeTime.of(2, MINUTES);
	
	@Test
	public void initialisesTimeAtStart() throws Exception {
		List<StopPath> starts = someStartPaths();
		ArrivalTimes times = timesFromPaths(starts, someTime(), onlyStartStops);

		Time time = times.get(startStop());

		assertThat(time, is(oneMinuteLater()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void initialisesOtherStops() throws Exception {
		BiConsumer<Stop, Time> consumer = mock(BiConsumer.class);
		ArrivalTimes times = timesFromPaths(someStartPaths(), someTime(), onlyStartStops);

		times.initialise(consumer);

		verify(consumer).accept(startStop(), oneMinuteLater());
		verify(consumer).accept(anotherStartStop(), twoMinutesLater());
	}

	@Test
	public void returnsInfiniteWhenTimeHasNotBeenSet() throws Exception {
		ArrivalTimes times = times(onlyStartStops);

		Time time = times.getConsideringMinimumChangeTime(targetStop());

		assertThat(time, is(equalTo(Time.future)));
	}

	@Test
	public void returnsSetTimeAfterTimeHasBeenSet() throws Exception {
		ArrivalTimes times = times(additionalStops);

		Stop stop = targetStop();
		Time timeToSet = someTime();
		times.set(stop, timeToSet);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(timeToSet)));
	}

	@Test
	public void returnsSetTimeWhenTimesContainsSeveralTimes() throws Exception {
		Stop stop0 = targetStop();
		Stop stop1 = anotherTargetStop();
		Time timeToSetForStop0 = someTime();
		Time timeToSetForStop1 = oneMinuteLater();
		int numberOfStops = additionalStops;

		ArrivalTimes times = times(numberOfStops);
		times.set(stop0, timeToSetForStop0);
		times.set(stop1, timeToSetForStop1);
		Time timeForStop0 = times.getConsideringMinimumChangeTime(stop0);
		Time timeForStop1 = times.getConsideringMinimumChangeTime(stop1);

		assertThat(timeForStop0, is(equalTo(timeToSetForStop0)));
		assertThat(timeForStop1, is(equalTo(timeToSetForStop1)));
	}

	@Test
	public void returnsInfiniteForStopsExceptStartStop() throws Exception {
		Stop stop = targetStop();

		ArrivalTimes times = timesFromPaths(noWalkTime(), someTime(), onlyStartStops);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(future)));
	}

	@Test
	public void returnsInfiniteWhenInternalStopIdIsTooHigh() throws Exception {
		int tooHighIndex = 2;
		Stop stop = buildTargetStop().withId(tooHighIndex).build();

		ArrivalTimes times = times(onlyStartStops);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(Time.future)));
	}

	@Test
	public void returnsInfiniteWhenInternalStopIdIsTooLow() throws Exception {
		int tooLowIndex = -1;
		Stop stop = stop().withId(tooLowIndex).build();

		ArrivalTimes times = times(onlyStartStops);
		Time time = times.getConsideringMinimumChangeTime(stop);

		assertThat(time, is(equalTo(Time.future)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeAtStartStops() throws Exception {
		Time timeAtStart = oneMinuteLater();
		ArrivalTimes times = timesFromPaths(noWalkTime(), timeAtStart, onlyStartStops);
		times.set(startStop(), timeAtStart);

		assertThat(times.getConsideringMinimumChangeTime(startStop()), is(equalTo(timeAtStart)));
		assertThat(times.getConsideringMinimumChangeTime(anotherStartStop()), is(equalTo(timeAtStart)));
	}

	@Test
	public void considersMinimumChangeTimeAtGivenStopOtherThanStart() throws Exception {
		Stop otherStop = targetStop(changeTime);
		Time timeAtStart = someTime();
		Time timeAtOther = oneMinuteLater();
		ArrivalTimes times = timesFromPaths(noWalkTime(), timeAtStart, additionalStops);
		times.set(otherStop, timeAtOther);

		Time timeAtOtherIncludingChangeTime = oneMinuteLater().plus(changeTime);
		assertThat(times.getConsideringMinimumChangeTime(otherStop),
				is(equalTo(timeAtOtherIncludingChangeTime)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeOnGetWithStartStop() throws Exception {
		Time timeAtStart = someTime();
		ArrivalTimes times = timesFromPaths(noWalkTime(), timeAtStart, onlyStartStops);

		assertThat(times.get(startStop()), is(equalTo(timeAtStart)));
	}

	@Test
	public void doesNotConsiderMinimumChangeTimeOnGet() throws Exception {
		Stop otherStop = targetStop();
		Time timeAtStart = someTime();
		Time timeAtOther = oneMinuteLater();
		ArrivalTimes times = timesFromPaths(noWalkTime(), timeAtStart, additionalStops);
		times.set(otherStop, timeAtOther);

		assertThat(times.get(otherStop), is(equalTo(timeAtOther)));
	}

	private List<StopPath> noWalkTime() {
		return asList(pathTo(startStop(), noMinutes),
				pathTo(anotherStartStop(), noMinutes));
	}

	private List<StopPath> someStartPaths() {
		return asList(pathTo(startStop(), oneMinute), pathTo(anotherStartStop(), twoMinutes));
	}

	private StopPath pathTo(Stop stop, RelativeTime walkTime) {
		return new StopPath(stop, walkTime);
	}

	private Stop startStop() {
		return stop().withId(0).withName("start").minimumChangeTime(changeTime).build();
	}

	private Stop anotherStartStop() {
		return stop().withId(1).withName("another start").build();
	}

	private Stop targetStop() {
		return buildTargetStop().build();
	}
	
	private Stop targetStop(RelativeTime changetime) {
		return buildTargetStop().minimumChangeTime(changetime).build();
	}

	private StopBuilder buildTargetStop() {
		return stop().withId(2).withName("target stop");
	}

	private Stop anotherTargetStop() {
		return stop().withId(3).withName("another target stop").build();
	}

	private static Time oneMinuteLater() {
		return someTime().plus(oneMinute);
	}

	private static Time twoMinutesLater() {
		return someTime().plus(twoMinutes);
	}

	private ArrivalTimes times(int numberOfStops) {
		Time someTime = someTime();
		return times(someTime, numberOfStops);
	}

	private ArrivalTimes times(Time departure, int numberOfStops) {
		return timesFromPaths(someStartPaths(), departure, numberOfStops);
	}

	private ArrivalTimes timesFromPaths(List<StopPath> fromStarts, Time departure, int numberOfStops) {
		StopPaths starts = DefaultStopPaths.from(fromStarts);
		return timesFromPaths(starts, departure, numberOfStops);
	}

	private ArrivalTimes timesFromPaths(
			StopPaths starts, Time timeAtStart, int totalNumberOfStops) {
		return MultipleStarts.create(starts, timeAtStart, totalNumberOfStops);
	}
}
