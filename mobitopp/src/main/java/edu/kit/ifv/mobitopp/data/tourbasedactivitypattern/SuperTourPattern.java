package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class SuperTourPattern implements TourBasedActivityPatternElement {

	
	public final DayOfWeek day;
	public final Activity surrogateHomeActivity;
	public final List<Activity> outboundTripActivities;
	public final List<TourPattern> nonlocalActivityPattern;
	public final List<Activity> inboundTripActivities;
	
	public SuperTourPattern(
		final DayOfWeek day,
		final Activity surrogateHomeActivity, 
		final List<Activity> outboundTripActivities,
		final List<TourPattern> nonlocalActivityPattern,
		final List<Activity> inboundTripActivities
	) {
		this.day = day;
		this.surrogateHomeActivity = surrogateHomeActivity;
		this.outboundTripActivities = Collections.unmodifiableList(outboundTripActivities);
		this.inboundTripActivities = Collections.unmodifiableList(inboundTripActivities);
		this.nonlocalActivityPattern = Collections.unmodifiableList(nonlocalActivityPattern);
	}

	public DayOfWeek startDay() {
		return  day;
	}

	@Override
	public Time start() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Activity> asActivities() {
		// TODO Auto-generated method stub
		return null;
	}

}
