package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class SimpleActivity implements Activity {
	
	private ActivityType  activityType;
	private Time  plannedStart;
	private RelativeTime  plannedDuration;
	private RelativeTime  expectedTripDuration;
	
	public SimpleActivity(ActivityType activityType, Time plannedStart, RelativeTime plannedDuration, RelativeTime expectedTripDuration) {
		this.activityType = activityType;
		this.plannedStart = plannedStart;
		this.plannedDuration = plannedDuration;
		this.expectedTripDuration = expectedTripDuration;
	}

	@Override
	public String toString() {
		return "SimpleActivity(" + activityType + "," + plannedStart + "," + plannedDuration + ")";
	}

	@Override
	public Time plannedStart() {
		return plannedStart;
	}
	
	@Override
	public RelativeTime plannedDuration() {
		return plannedDuration;
	}
	
	@Override
	public ActivityType activityType() {
		return activityType;
	}
	
	@Override
	public RelativeTime expectedTripDuration() {
		return expectedTripDuration;
	}
	

}
