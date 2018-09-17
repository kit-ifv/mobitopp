package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class SplitActivity implements Activity {
	
	
	private final ActivityType  activityType;
	private final List<SimpleActivity>  parts;
	
	private SplitActivity(ActivityType activityType,  List<SimpleActivity> parts) {
		this.activityType = activityType;
		this.parts = Collections.unmodifiableList(parts);
	}

	public static SplitActivity fromPatternActivities(ActivityType typeOfMainActivity, List<? extends PatternActivity> activities) {
		
		List<SimpleActivity> tmp = 
											activities.stream()
											.map(x -> SimpleActivity.fromPatternActivity(x))
											.collect(Collectors.toList());
		 
		assert tmp.size() >= 2 : ("\n" + activities + "\n");
		 
		return new SplitActivity(typeOfMainActivity,tmp);
	}

	@Override
	public Time plannedStart() {
		return parts.get(0).plannedStart();
	}

	@Override
	public RelativeTime plannedDuration() {
		Activity first = parts.get(0);
		Activity last = parts.get(parts.size()-1);
		
		return last.plannedStart().plus(last.plannedDuration()).differenceTo(first.plannedStart());
	}

	@Override
	public ActivityType activityType() {
		return activityType;
	}

	@Override
	public RelativeTime expectedTripDuration() {

		return parts.get(0).expectedTripDuration();
	}
	
	public List<SimpleActivity> parts() {
		return parts;
	}
	
	@Override
	public String toString() {
		return "SplitActivity[" + activityType + "," + parts.size() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityType == null) ? 0 : activityType.hashCode());
		result = prime * result + ((parts == null) ? 0 : parts.hashCode());
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
		SplitActivity other = (SplitActivity) obj;
		if (activityType != other.activityType)
			return false;
		if (parts == null) {
			if (other.parts != null)
				return false;
		} else if (!parts.equals(other.parts))
			return false;
		return true;
	}

	
}
