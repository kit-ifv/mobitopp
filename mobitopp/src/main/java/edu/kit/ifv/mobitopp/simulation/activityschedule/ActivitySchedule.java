package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.List;
import java.util.Set;


public interface ActivitySchedule {

	
	boolean isEmpty();
	
	ActivityIfc firstActivity();
	
	boolean hasPrevActivity(ActivityIfc activity);
	boolean hasNextActivity(ActivityIfc activity);

	ActivityIfc nextActivity(ActivityIfc activity);

	ActivityIfc prevActivity(ActivityIfc activity);
	
	ActivityIfc nextHomeActivity(ActivityIfc activity);
	
	int activityNrByType(ActivityIfc activity);
	Set<Integer> alreadyVisitedZonesByActivityType(ActivityIfc activity);

	List<ActivityIfc> activitiesBetween(ActivityIfc previousActivity, ActivityIfc mainActivity);
}