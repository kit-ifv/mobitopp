package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.List;

public interface ActivitySequence
{

	
	public ActivityIfc firstActivity();
	public ActivityIfc lastActivity();

	public ActivityIfc nextActivity(ActivityIfc activity);
	public ActivityIfc prevActivity(ActivityIfc activity);


	public void addAsLastActivity(ActivityIfc activity);
	public void addAsFirstActivity(ActivityIfc activity);
	public void insertAfter(ActivityIfc previous, ActivityIfc toBeInserted);
 
	public void removeActivity(ActivityIfc activity);

	List<ActivityIfc> activitiesBetween(ActivityIfc previousActivity, ActivityIfc mainActivity);
 
}
