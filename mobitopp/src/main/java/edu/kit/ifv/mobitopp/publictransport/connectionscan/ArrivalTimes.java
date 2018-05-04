package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.function.BiConsumer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

interface ArrivalTimes {

	void initialise(BiConsumer<Stop, Time> consumer);

	void set(Stop stop, Time time);

	Time getConsideringMinimumChangeTime(Stop stop);

	Time get(Stop stop);

	Time startTime();

}