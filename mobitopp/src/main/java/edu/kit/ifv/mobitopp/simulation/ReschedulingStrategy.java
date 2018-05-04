package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public interface ReschedulingStrategy {

	void adjustSchedule(
		ModifiableActivitySchedule activitySchedule, 
		ActivityIfc beginningActivity, 
		Time plannedStartTime,
		Time currentTime
	);

}
