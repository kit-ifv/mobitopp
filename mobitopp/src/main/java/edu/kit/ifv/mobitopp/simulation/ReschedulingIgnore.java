package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

class ReschedulingIgnore
 implements ReschedulingStrategy 
{

	public void adjustSchedule(
		ModifiableActivitySchedule schedule, 
		ActivityIfc beginningActivity, 
		Time plannedStartTime,
		Time currentTime
	) {

		if (!schedule.checkOrderInvariant()
				|| !schedule.checkNonOverlappingInvariant()
		) {

			schedule.fixStartTimeOfActivities();
		}

		assert schedule.checkOrderInvariant();
		assert schedule.checkNonOverlappingInvariant();
  }

}
