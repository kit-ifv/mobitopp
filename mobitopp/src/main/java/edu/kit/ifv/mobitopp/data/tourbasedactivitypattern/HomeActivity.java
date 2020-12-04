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

	@Override
	public List<ExtendedPatternActivity> asPatternActivities(int i) {
		return Arrays.asList(ExtendedPatternActivity.fromActivity(i,false,this));
	}

	public static TourBasedActivityPatternElement fromExtendedPatternActivities(List<ExtendedPatternActivity> tour) {
		assert tour.size()== 1;
		ExtendedPatternActivity act = tour.get(0);
		
		return new HomeActivity(act.getWeekDayType(),SimpleActivity.fromPatternActivity(act));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((startDay == null) ? 0 : startDay.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HomeActivity other = (HomeActivity) obj;
		if (startDay != other.startDay)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HomeActivity [startDay=" + startDay + "," + super.toString() + "]";
	}

	
}
