package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class TourPattern implements TourBasedActivityPatternElement {
	
	public final DayOfWeek day;
	public final Time expectedEnd;
	public final Activity mainActivity;
	public final List<Activity> outboundTripActivities;
	public final List<Activity> inboundTripActivities;
	public final List<Activity> subtourActivities;
	
	public TourPattern(
		DayOfWeek day,
		Activity mainActivity, 
		Time expectedEnd,
		List<Activity> outboundTripActivities,
		List<Activity> inboundTripActivities,
		List<Activity> subtourActivities
	) {
		this.day = day;
		this.mainActivity = mainActivity;
		this.expectedEnd = expectedEnd;
		this.outboundTripActivities = Collections.unmodifiableList(outboundTripActivities);
		this.inboundTripActivities = Collections.unmodifiableList(inboundTripActivities);
		this.subtourActivities = Collections.unmodifiableList(subtourActivities);
	}

	@Override
	public DayOfWeek startDay() {
		return day;
	}
	
	@Override
	public String toString() {
		return "TourPattern( " + day + ", " + mainActivity 
	//			+ ", " + expectedEnd 
				+ ", ["  
					+  outboundTripActivities.size() + "," 
					+  inboundTripActivities.size() + "," 
					+  subtourActivities.size() 
//					+ "\n" +  outboundTripActivities
//					+ "\n" +  inboundTripActivities
//					+ "\n" +  subtourActivities
				+ "])";
	}

}
