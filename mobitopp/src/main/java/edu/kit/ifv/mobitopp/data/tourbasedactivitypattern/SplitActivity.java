package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
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
											.filter(x -> x.getActivityType() == typeOfMainActivity)
											.map(x -> new SimpleActivity(typeOfMainActivity, SimpleTime.ofMinutes(x.getStarttime()),RelativeTime.ofMinutes(x.getDuration()), RelativeTime.ofMinutes(x.getObservedTripDuration())))
											.collect(Collectors.toList());
		 
		assert tmp.size() >= 2;
		 
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

}
