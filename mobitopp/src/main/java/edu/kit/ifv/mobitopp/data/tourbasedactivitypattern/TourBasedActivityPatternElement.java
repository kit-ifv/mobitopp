package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.List;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public interface TourBasedActivityPatternElement {
	
	DayOfWeek startDay();
	
	Time start();

	List<Activity> asActivities();

	List<ExtendedPatternActivity> asPatternActivities(int i);
		
}
