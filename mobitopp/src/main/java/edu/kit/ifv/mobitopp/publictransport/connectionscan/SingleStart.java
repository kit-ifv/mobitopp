package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

class SingleStart extends BaseTimes {

	private final Stop start;
	private final Time startTime;

	private SingleStart(Stop start, Time startTime, int numberOfStops) {
		super(numberOfStops);
		this.start = start;
		this.startTime = startTime;
		initialise();
	}

	static ArrivalTimes create(Stop start, Time departure, int numberOfStops) {
		return new SingleStart(start, departure, numberOfStops);
	}
	
	@Override
	public Time startTime() {
		return startTime;
	}

	@Override
	protected void initialiseStart() {
		set(start, startTime);
	}

	@Override
	public void initialise(BiConsumer<Stop, Time> consumer) {
		consumer.accept(start, startTime);
	}

	@Override
	protected boolean isStart(Stop stop) {
		return start.equals(stop);
	}

}