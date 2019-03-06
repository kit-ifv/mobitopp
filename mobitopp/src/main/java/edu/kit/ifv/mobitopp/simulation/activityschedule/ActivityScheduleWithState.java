package edu.kit.ifv.mobitopp.simulation.activityschedule;

import edu.kit.ifv.mobitopp.simulation.Trip;

public interface ActivityScheduleWithState extends ActivitySchedule {

	void setCurrentTrip(Trip trip);

	Trip currentTrip();

	void startActivity(ActivityIfc activity);

	ActivityIfc currentActivity();

}