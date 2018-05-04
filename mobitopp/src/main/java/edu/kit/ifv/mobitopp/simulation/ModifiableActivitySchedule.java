package edu.kit.ifv.mobitopp.simulation;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivitySchedule;
import edu.kit.ifv.mobitopp.time.Time;

public interface ModifiableActivitySchedule extends ActivitySchedule {
	
	void removeActivity(ActivityIfc activity);

	boolean checkOrderInvariant();

	boolean checkNonOverlappingInvariant();

	void fixStartTimeOfActivities();

	List<ActivityIfc> getSucceedingActivitiesUntilDate(ActivityIfc activity, Time time);

}