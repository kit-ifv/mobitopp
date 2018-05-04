package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

abstract class BaseTimes implements ArrivalTimes {

	private final Time[] times;

	BaseTimes(int numberOfStops) {
		super();
		times = new Time[numberOfStops];
	}

	protected void initialise() {
		for (int i = 0; i < times.length; i++) {
			times[i] = Time.future;
		}
		initialiseStart();
	}

	protected abstract void initialiseStart();

	@Override
	public void set(Stop stop, Time time) {
		times[stop.id()] = time;
	}

	@Override
	public Time getConsideringMinimumChangeTime(Stop stop) {
		if (isStart(stop)) {
			return get(stop);
		}
		return considerChangeTime(stop);
	}

	protected abstract boolean isStart(Stop stop);

	private Time considerChangeTime(Stop stop) {
		return stop.addChangeTimeTo(get(stop));
	}

	@Override
	public Time get(Stop stop) {
		int internal = stop.id();
		if (internal >= times.length || internal < 0) {
			return Time.future;
		}
		return times[internal];
	}

}