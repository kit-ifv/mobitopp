package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;
import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.publictransport.model.StopPath;
import edu.kit.ifv.mobitopp.time.Time;

class MultipleStarts extends BaseTimes {

	private final Time startTime;
	private final List<StopPath> startPaths;

	private MultipleStarts(List<StopPath> startPaths, Time startTime, int numberOfStops) {
		super(numberOfStops);
		this.startPaths = startPaths;
		this.startTime = startTime;
		initialise();
	}

	static ArrivalTimes create(StopPaths fromStarts, Time startTime, int numberOfStops) {
		return new MultipleStarts(fromStarts.stopPaths(), startTime, numberOfStops);
	}

	@Override
	public Time startTime() {
		return startTime;
	}

	@Override
	protected void initialiseStart() {
		for (StopPath pathToStop : startPaths) {
			set(pathToStop.stop(), startTime.plus(pathToStop.duration()));
		}
	}

	@Override
	public void initialise(BiConsumer<Stop, Time> consumer) {
		for (StopPath pathToStop : startPaths) {
			consumer.accept(pathToStop.stop(), startTime.plus(pathToStop.duration()));
		}
	}

	@Override
	protected boolean isStart(Stop stop) {
		for (StopPath pathToStop : startPaths) {
			if (pathToStop.stop().equals(stop)) {
				return true;
			}
		}
		return false;
	}

}