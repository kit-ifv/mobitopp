package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.data.InputSpecification;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimulationDays implements InputSpecification {

	private static final SimpleTime simulationStart = new SimpleTime();
	private final Time start;
	private List<Time> period;

	public SimulationDays(List<Time> period) {
		this.start = simulationStart();
		this.period = period;
	}

	@Override
	public Time startDate() {
		return start;
	}

	@Override
	public List<Time> simulationDates() {
		return period;
	}

	public static SimulationDays containing(int days) {
		verify(days);
		Time start = simulationStart();
		List<Time> period = new ArrayList<>();
		for (int day = 0; day < days; day++) {
			period.add(start.plusDays(day));
		}
		return new SimulationDays(period);
	}

	private static void verify(int days) {
		if (0 >= days) {
			throw warn(new IllegalArgumentException("At least one simulation day is required, but was: " + days), log);
		}
	}

	public static Time simulationStart() {
		return simulationStart;
	}

	public Time endDate() {
		return period.get(period.size() - 1).nextDay();
	}

	public List<Time> toList() {
		return Collections.unmodifiableList(period);
	}
}
