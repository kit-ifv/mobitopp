package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class TourPattern implements TourBasedActivityPatternElement {
	
	public final DayOfWeek day;
	public final Time expectedEnd;
	public final Activity mainActivity;
	public final List<Activity> outboundTripActivities;
	public final List<Activity> inboundTripActivities;
	public final List<List<Activity>> subtourActivities;
	
	private static final RelativeTime TRIP_OFFSET = RelativeTime.ofMinutes(30);
	
	public TourPattern(
		DayOfWeek day,
		Activity mainActivity, 
		Time expectedEnd,
		List<Activity> outboundTripActivities,
		List<Activity> inboundTripActivities,
		List<List<Activity>> subtourActivities
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

	@Override
	public Time start() {
		
		if (!outboundTripActivities.isEmpty()) {
			Activity next = outboundTripActivities.get(0);
			return next.plannedStart().minus(next.expectedTripDuration());
		}
		
		return mainActivity.plannedStart().minus(mainActivity.expectedTripDuration());
	}
	
	@Override
	public List<Activity> asActivities() {
		List<Activity> activities = new ArrayList<Activity>();
		
		activities.addAll(outboundTripActivities);
		
		if (numberOfSubtours() == 0) {
			activities.add(mainActivity);
		} else {
			
			assert mainActivity instanceof SplitActivity;
			
			assert numberOfSubtours()+1 == ((SplitActivity)mainActivity).parts().size();

			List<SimpleActivity> parts =  ((SplitActivity)mainActivity).parts();
			
			for(int i=0; i<numberOfSubtours(); i++) {
				activities.add(parts.get(i));
				
				for(Activity subtourActivity :  subtourActivities.get(i)) {
					activities.add(subtourActivity);
				}
				
			}
			activities.add(parts.get(numberOfSubtours()+1));
			
		}
		
		activities.addAll(inboundTripActivities);
	
		return activities;
	}
	
	private int numberOfSubtours() {
		return subtourActivities.size();
	}

}
