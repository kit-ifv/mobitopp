package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public enum DayType {
	weekdays, saturday, sunday;

	public static DayType from(Time time) {
		if (DayOfWeek.SUNDAY.equals(time.weekDay())) {
			return sunday;
		}
		if (DayOfWeek.SATURDAY.equals(time.weekDay())) {
			return saturday;
		}
		return weekdays;
	}

}
