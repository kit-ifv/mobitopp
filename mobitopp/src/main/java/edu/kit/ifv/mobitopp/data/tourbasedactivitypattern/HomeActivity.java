package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.Arrays;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class HomeActivity 
extends SimpleActivity
implements Activity, TourBasedActivityPatternElement {
	
	private final DayOfWeek startDay;

	public HomeActivity(DayOfWeek startDay, ActivityType activityType, Time plannedStart, RelativeTime plannedDuration,
			RelativeTime expectedTripDuration) {
		super(activityType, plannedStart, plannedDuration, expectedTripDuration);
		this.startDay = startDay;
	}
	
	public HomeActivity(DayOfWeek startDay, SimpleActivity act) {
		this(startDay, act.activityType(), act.plannedStart(), act.plannedDuration(),act.expectedTripDuration());
	}

	@Override
	public Time start() {
		return plannedStart();
	}

	@Override
	public List<Activity> asActivities() {
		
		return Arrays.asList(this);
	}

	@Override
	public DayOfWeek startDay() {
		return startDay;
	}

}
