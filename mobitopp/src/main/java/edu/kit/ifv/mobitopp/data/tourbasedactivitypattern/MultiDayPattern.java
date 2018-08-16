package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class MultiDayPattern implements TourBasedActivityPatternElement {

	public final DayOfWeek startDay;
	public final DayOfWeek endDay;
	
	public final Activity surrogateHomeActivity;
	public final List<TourPattern> toursBefore;
	public final List<Activity> outboundTripActivities;
	public final List<DayTourPattern> nonlocalActivityPattern;
	public final List<Activity> inboundTripActivities;
	public final List<TourPattern> toursAfter;
	
	public MultiDayPattern(
		final DayOfWeek startDay,
		final DayOfWeek endDay,
		final Activity surrogateHomeActivity, 
		final List<TourPattern> toursBefore,
		final List<Activity> outboundTripActivities,
		final List<DayTourPattern> nonlocalActivityPattern,
		final List<Activity> inboundTripActivities,
		final List<TourPattern> toursAfter
	) {
		this.startDay = startDay;
		this.endDay = endDay;
		this.surrogateHomeActivity = surrogateHomeActivity;
		this.outboundTripActivities = Collections.unmodifiableList(outboundTripActivities);
		this.inboundTripActivities = Collections.unmodifiableList(inboundTripActivities);
		this.nonlocalActivityPattern = Collections.unmodifiableList(nonlocalActivityPattern);
		this.toursBefore = Collections.unmodifiableList(toursBefore);
		this.toursAfter = Collections.unmodifiableList(toursAfter);
	}

}
