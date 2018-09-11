package edu.kit.ifv.mobitopp.time;

public interface Time extends Comparable<Time> {

	Time start = new SimpleTime();
	Time future = SimpleTime.ofDays(4000);
	
	int getWeek();
	int getDay();
	int getHour();
	int getMinute();
	int getSecond();
	long toSeconds();
	long toMinutes();

	Time previousDay();
	Time nextDay();

	DayOfWeek weekDay();

	boolean isBefore(Time otherDate);
	boolean isBeforeOrEqualTo(Time otherDate);
	boolean isAfter(Time otherDate);
	boolean isAfterOrEqualTo(Time otherDate);
	boolean isMidnight();

	Time plus(RelativeTime increment);
	Time plusWeeks(int increment);
	Time plusDays(int increment);
	Time plusHours(int increment);
	Time plusMinutes(int increment);
	Time plusSeconds(int increment);
	Time minus(RelativeTime decrement);
	Time minusWeeks(int decrement);
	Time minusDays(int decrement);
	Time minusHours(int decrement);
	Time minusMinutes(int decrement);
	Time minusSeconds(int decrement);

	Time startOfDay();
	Time newTime(int hour, int minute, int second);

	RelativeTime differenceTo(Time otherDate);
	
	RelativeTime fromStart();

}
