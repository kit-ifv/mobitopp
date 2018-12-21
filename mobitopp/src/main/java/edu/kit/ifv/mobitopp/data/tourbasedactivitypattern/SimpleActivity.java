package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import edu.kit.ifv.mobitopp.data.PatternActivity;
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
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityType == null) ? 0 : activityType.hashCode());
		result = prime * result + ((expectedTripDuration == null) ? 0 : expectedTripDuration.hashCode());
		result = prime * result + ((plannedDuration == null) ? 0 : plannedDuration.hashCode());
		result = prime * result + ((plannedStart == null) ? 0 : plannedStart.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleActivity other = (SimpleActivity) obj;
		if (activityType != other.activityType)
			return false;
		if (expectedTripDuration == null) {
			if (other.expectedTripDuration != null)
				return false;
		} else if (!expectedTripDuration.equals(other.expectedTripDuration))
			return false;
		if (plannedDuration == null) {
			if (other.plannedDuration != null)
				return false;
		} else if (!plannedDuration.equals(other.plannedDuration))
			return false;
		if (plannedStart == null) {
			if (other.plannedStart != null)
				return false;
		} else if (!plannedStart.equals(other.plannedStart))
			return false;
		return true;
	}

	public static SimpleActivity fromPatternActivity(PatternActivity activity) {
	
		return new SimpleActivity(
									activity.getActivityType(), 
									activity.startTime(),
									RelativeTime.ofMinutes(activity.getDuration()),
									RelativeTime.ofMinutes(activity.getObservedTripDuration())
						);
	}

	

}
