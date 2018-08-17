package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class SimpleActivity implements Activity {
	
	private ActivityType  activityType;
	private Time  plannedStart;
	private RelativeTime  plannedDuration;
	
	public SimpleActivity(ActivityType activityType, Time plannedStart, RelativeTime plannedDuration) {
		this.activityType = activityType;
		this.plannedStart = plannedStart;
		this.plannedDuration = plannedDuration;
	}

	@Override
	public String toString() {
		return "SimpleActivity(" + activityType + "," + plannedStart + "," + plannedDuration + ")";
	}
	

}
