package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class DayStayAtHomePattern implements DayPattern {

	public final DayOfWeek day;
	
	public DayStayAtHomePattern(DayOfWeek day) {
		this.day = day;
	}
}
