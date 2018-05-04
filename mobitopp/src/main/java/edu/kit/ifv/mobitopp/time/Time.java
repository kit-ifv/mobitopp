package edu.kit.ifv.mobitopp.time;

public interface Time extends Comparable<Time> {

	Time future = SimpleTime.ofDays(4000);
	
	int getDay();
	int getHour();
	int getMinute();
	int getSecond();
	long toSeconds();

	Time previousDay();
	Time nextDay();

	DayOfWeek weekDay();

	boolean isBefore(Time otherDate);
	boolean isBeforeOrEqualTo(Time otherDate);
	boolean isAfter(Time otherDate);
	boolean isAfterOrEqualTo(Time otherDate);
	boolean isMidnight();

	Time plus(RelativeTime increment);
	Time plusDays(int increment);
	Time plusHours(int increment);
	Time plusMinutes(int increment);
	Time plusSeconds(int increment);
	Time minus(RelativeTime decrement);
	Time minusDays(int decrement);

	Time startOfDay();
	Time newTime(int hour, int minute, int second);

	RelativeTime differenceTo(Time otherDate);
	
	RelativeTime fromStart();

}
