package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

import static java.time.temporal.ChronoUnit.MINUTES;

public class Activity {
	
	private ActivityType  activityType;
	private Time  plannedStart;
	private RelativeTime  plannedDuration;
	
	public Activity(ActivityType activityType, Time plannedStart, RelativeTime plannedDuration) {
		this.activityType = activityType;
		this.plannedStart = plannedStart;
		this.plannedDuration = plannedDuration;
	}

}
