package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.List;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.ZoneId;


public interface ActivitySchedule {

	
	boolean isEmpty();
	
	ActivityIfc firstActivity();
	
	boolean hasPrevActivity(ActivityIfc activity);
	boolean hasNextActivity(ActivityIfc activity);

	ActivityIfc nextActivity(ActivityIfc activity);

	ActivityIfc prevActivity(ActivityIfc activity);
	
	ActivityIfc nextHomeActivity(ActivityIfc activity);
	
	int activityNrByType(ActivityIfc activity);
	
	Set<ZoneId> alreadyVisitedZonesByActivityType(ActivityIfc activity);

	List<ActivityIfc> activitiesBetween(ActivityIfc previousActivity, ActivityIfc mainActivity);
}