package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class DayTourPattern implements DayPattern {

	public final DayOfWeek day;
	public final List<TourPattern> dayPattern;
	
	public DayTourPattern(DayOfWeek day, List<TourPattern> dayPattern) {
		assert !dayPattern.isEmpty();
	
		this.day = day;
		this.dayPattern = Collections.unmodifiableList(dayPattern);
	}

	/*
	private static List<Activity> noActivities = Collections.emptyList();
	
	public final Activity mainActivity;
	public final List<Activity> outboundTripActivities;
	public final List<Activity> inboundTripActivities;
	public final List<Activity> subtourActivities;
	
	public DayPattern(
		Activity mainActivity, 
		List<Activity> outboundTripActivities,
		List<Activity> inboundTripActivities,
		List<Activity> subtourActivities
	) {
		this.mainActivity = mainActivity;
		this.outboundTripActivities = Collections.unmodifiableList(outboundTripActivities);
		this.inboundTripActivities = Collections.unmodifiableList(inboundTripActivities);
		this.subtourActivities = Collections.unmodifiableList(subtourActivities);
	}
	
	public static DayPattern SimpleDay(Activity activity) {
			return new DayPattern(activity, noActivities, noActivities, noActivities);
	}
	*/

}
