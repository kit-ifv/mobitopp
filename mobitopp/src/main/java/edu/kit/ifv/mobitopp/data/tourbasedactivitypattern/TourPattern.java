package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.Collections;
import java.util.List;

public class TourPattern implements TourBasedActivityPatternElement {
	
	public final Activity mainActivity;
	public final List<Activity> outboundTripActivities;
	public final List<Activity> inboundTripActivities;
	public final List<Activity> subtourActivities;
	
	public TourPattern(
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

}
