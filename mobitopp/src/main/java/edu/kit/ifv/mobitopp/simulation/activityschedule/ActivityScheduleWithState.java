package edu.kit.ifv.mobitopp.simulation.activityschedule;

import edu.kit.ifv.mobitopp.simulation.TripIfc;

public interface ActivityScheduleWithState extends ActivitySchedule {

	void setCurrentTrip(TripIfc trip);

	TripIfc currentTrip();

	void startActivity(ActivityIfc activity);

	ActivityIfc currentActivity();

}