package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumStopPoint;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.publictransport.model.TransferWalkTime;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.publictransport.VisumWalkTime;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;
import edu.kit.ifv.mobitopp.visum.StopArea;
import edu.kit.ifv.mobitopp.visum.StopAreaPair;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumPtTransferWalkTimes;

public class VisumWalkTimeTest {

	private static final String notNeeded = "";

	@After
	public void cleanUp() throws NoSuchFieldException, SecurityException, IllegalAccessException {
		ReflectionHelper.clearTransportSystemSetCache();
	}

	@Test
	public void returnsEmptyOptionalWhenThereAreNoStopsToReach() throws Exception {
		TransferWalkTime walkTime = VisumWalkTime.from(emptyMap(), emptyList());
		Stop start = someStop();
		Stop end = anotherStop();

		Optional<RelativeTime> duration = walkTime.walkTime(start, end);

		assertThat(duration, isEmpty());
	}

	@Test
	public void returnsDurationForStopsWhichAreInSameVisumStopArea() throws Exception {
		TransferWalkTime walkTime = VisumWalkTime.from(sameStopArea(), sameAreaStopPoints());
		Stop start = stop().withExternalId(1).withName("start").build();
		Stop end = stop().withExternalId(2).withName("end").build();

		Optional<RelativeTime> duration = walkTime.walkTime(start, end);

		assertThat(duration, isPresent());
		assertThat(duration, hasValue(equalTo(RelativeTime.of(1, MINUTES))));
	}

	private static Map<StopAreaPair, VisumPtTransferWalkTimes> sameStopArea() {
		HashMap<StopAreaPair, VisumPtTransferWalkTimes> times = new HashMap<>();
		int fromAreaId = 1;
		int toAreaId = 1;
		int seconds = 60;
		times.put(key(fromAreaId, toAreaId), transferTime(fromAreaId, toAreaId, seconds));
		return times;
	}

	private Collection<VisumPtStopPoint> sameAreaStopPoints() {
		VisumPtStopArea sameArea = stopArea(1);
		VisumPtStopPoint visumStartStop = visumStopPoint().withId(1).with(sameArea).build();
		VisumPtStopPoint visumEndStop = visumStopPoint().withId(2).with(sameArea).build();
		return asList(visumStartStop, visumEndStop);
	}

	@Test
	public void returnsDurationForStopsWhichAreInDifferentStopAreasButAreReachableByFoot()
			throws Exception {
		Stop start = someStop();
		Stop end = anotherStop();
		TransferWalkTime walkTime = VisumWalkTime.from(differentStopAreas(),
				differentAreasStopPoints(start, end));

		Optional<RelativeTime> duration = walkTime.walkTime(start, end);

		assertThat(duration, isPresent());
		assertThat(duration, hasValue(equalTo(RelativeTime.of(3, MINUTES))));
	}

	private Collection<VisumPtStopPoint> differentAreasStopPoints(Stop start, Stop end) {
		return differentAreasStopPoints(start.externalId(), end.externalId());
	}

	private Map<StopAreaPair, VisumPtTransferWalkTimes> differentStopAreas() {
		HashMap<StopAreaPair, VisumPtTransferWalkTimes> times = new HashMap<>();
		int fromAreaId = 1;
		int toAreaId = 2;
		int seconds = 180;
		times.put(key(fromAreaId, toAreaId), transferTime(fromAreaId, toAreaId, seconds));
		return times;
	}

	@Test
	public void returnsNoDurationForStopsWhichAreInDifferentStopAreasAndAreNotReachableByFoot()
			throws Exception {
		Stop start = someStop();
		Stop end = anotherStop();
		TransferWalkTime walkTime = VisumWalkTime.from(unreachableStopAreas(),
				differentAreasStopPoints(start, end));

		Optional<RelativeTime> duration = walkTime.walkTime(start, end);

		assertThat(duration, isEmpty());
	}

	private Map<StopAreaPair, VisumPtTransferWalkTimes> unreachableStopAreas() {
		HashMap<StopAreaPair, VisumPtTransferWalkTimes> times = new HashMap<>();
		int fromAreaId = 1;
		int toAreaId = 3;
		int seconds = 60;
		times.put(key(fromAreaId, toAreaId), transferTime(fromAreaId, toAreaId, seconds));
		return times;
	}

	@Test(expected = NoChangeTimeAvailable.class)
	public void failsWhenNoMinimumChangeTimeForStopAreaIsAvailable() throws Exception {
		TransferWalkTime times = VisumWalkTime.from(emptyMap(), emptyList());

		int stopId = 1;
		times.minimumChangeTime(stopId);
	}

	@Test
	public void returnsMinimumChangeTimeForStopWhichExists() throws Exception {
		TransferWalkTime times = VisumWalkTime.from(containsMinimumChangeTime(),
				singleStopPointWithMinimumChangeTime());

		int stopId = 1;
		RelativeTime minimumChangeTime = times.minimumChangeTime(stopId);

		assertThat(minimumChangeTime, is(equalTo(RelativeTime.of(1, MINUTES))));
	}

	private Collection<VisumPtStopPoint> singleStopPointWithMinimumChangeTime() {
		Stop stop = someStop();
		VisumPtStopArea stopArea = stopArea(stop.externalId());
		return asList(visumStopPoint().withId(someStop().externalId()).with(stopArea).build());
	}

	private static Map<StopAreaPair, VisumPtTransferWalkTimes> containsMinimumChangeTime() {
		HashMap<StopAreaPair, VisumPtTransferWalkTimes> changeTimes = new HashMap<>();
		int areaId = 1;
		int seconds = 60;
		changeTimes.put(key(areaId, areaId), transferTime(areaId, areaId, seconds));
		return changeTimes;
	}

	@Test
	public void returnsMinimumChangeTimeWhenSeveralStopsAreAvailable() throws Exception {
		int first = 1;
		int second = 2;
		TransferWalkTime times = VisumWalkTime.from(severalMinimumChangeTime(),
				differentAreasStopPoints(first, second));

		RelativeTime changeTimeAtFirstArea = times.minimumChangeTime(first);

		assertThat(changeTimeAtFirstArea, is(equalTo(RelativeTime.of(1, MINUTES))));

		RelativeTime changeTimeAtSecondArea = times.minimumChangeTime(second);
		assertThat(changeTimeAtSecondArea, is(equalTo(RelativeTime.of(2, MINUTES))));
	}

	private Collection<VisumPtStopPoint> differentAreasStopPoints(int first, int second) {
		VisumPtStopArea startArea = stopArea(first);
		VisumPtStopArea endArea = stopArea(second);
		VisumPtStopPoint visumStart = visumStopPoint().withId(first).with(startArea).build();
		VisumPtStopPoint visumEnd = visumStopPoint().withId(second).with(endArea).build();
		return asList(visumStart, visumEnd);
	}

	private static Map<StopAreaPair, VisumPtTransferWalkTimes> severalMinimumChangeTime() {
		HashMap<StopAreaPair, VisumPtTransferWalkTimes> changeTimes = new HashMap<>();
		int firstAreaId = 1;
		int firstTime = 60;
		changeTimes.put(key(firstAreaId, firstAreaId),
				transferTime(firstAreaId, firstAreaId, firstTime));

		int secondAreaId = 2;
		int secondTime = 120;
		changeTimes.put(key(secondAreaId, secondAreaId),
				transferTime(secondAreaId, secondAreaId, secondTime));
		return changeTimes;
	}

	private static StopAreaPair key(int fromAreaId, int toAreaId) {
		return new StopAreaPair(new StopArea(fromAreaId), new StopArea(toAreaId));
	}

	private static VisumPtTransferWalkTimes transferTime(int fromAreaId, int toAreaId, int seconds) {
		return new VisumPtTransferWalkTimes(stopArea(fromAreaId), stopArea(toAreaId), notNeeded,
				seconds);
	}

	private static VisumPtStopArea stopArea(int fromAreaId) {
		return new VisumPtStopArea(fromAreaId, null, notNeeded, notNeeded, null, 0, 0, 0);
	}

}
