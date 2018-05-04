package edu.kit.ifv.mobitopp.time;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SimpleTime implements Time, Comparable<Time> {

	private final long seconds;

	public SimpleTime() {
		super();
		this.seconds = 0;
	}
	
	public SimpleTime(long seconds) {
		super();
		this.seconds = seconds;
	}
	
	public static Time of(int days, int hours, int minutes, int seconds) {
		return from(RelativeTime.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds));
	}
	
	public static Time from(RelativeTime fromStart) {
		return ofSeconds(fromStart.seconds());
	}
	
	public static Time ofDays(long days) {
		return from(RelativeTime.ofDays(days));
	}
	
	public static Time ofHours(long hours) {
		return from(RelativeTime.ofHours(hours));
	}
	
	public static Time ofMinutes(long minutes) {
		return from(RelativeTime.ofMinutes(minutes));
	}
	
	public static Time ofSeconds(long seconds) {
		return new SimpleTime(seconds);
	}

	@Override
	public long toSeconds() {
		return this.seconds;
	}

	@Override
	public int getDay() {
		return (int) fromStart().toDays();
	}

	@Override
	public DayOfWeek weekDay() {
		return DayOfWeek.fromDay(getDay());
	}

	@Override
	public int getHour() {
		return (int) fromStart().toHours() % 24;
	}

	@Override
	public int getMinute() {
		return (int) (fromStart().toMinutes() % 60);
	}

	@Override
	public int getSecond() {
		return (int) fromStart().seconds() % 60;
	}

	@Override
	public Time previousDay() {
		return SimpleTime.ofDays(fromStart().toDays()).minusDays(1);
	}

	@Override
	public Time nextDay() {
		return SimpleTime.ofDays(fromStart().toDays()).plusDays(1);
	}

	@Override
	public boolean isMidnight() {
		return (getSecond() == 0) && (getMinute() == 0) && (getHour() == 0);
	}

	@Override
	public boolean isAfter(Time otherDate) {
		return toSeconds() > otherDate.toSeconds();
	}

	@Override
	public boolean isBefore(Time otherDate) {
		return toSeconds() < otherDate.toSeconds();
	}

	@Override
	public boolean isBeforeOrEqualTo(Time otherDate) {
		return toSeconds() <= otherDate.toSeconds();
	}
	
	@Override
	public boolean isAfterOrEqualTo(Time otherDate) {
		return toSeconds() >= otherDate.toSeconds();
	}

	@Override
	public Time minus(RelativeTime decrement) {
		RelativeTime changed = fromStart().minus(decrement);
		return from(changed);
	}

	@Override
	public Time minusDays(int decrement) {
		RelativeTime changed = fromStart().minusDays(decrement);
		return from(changed);
	}
	
	@Override
	public Time plus(RelativeTime increment) {
		RelativeTime changed = fromStart().plus(increment);
		return from(changed);
	}

	@Override
	public Time plusDays(int increment) {
		RelativeTime changed = fromStart().plusDays(increment);
		return from(changed);
	}

	@Override
	public Time plusHours(int increment) {
		RelativeTime changed = fromStart().plusHours(increment);
		return from(changed);
	}

	@Override
	public Time plusMinutes(int increment) {
		RelativeTime changed = fromStart().plusMinutes(increment);
		return from(changed);
	}

	@Override
	public Time plusSeconds(int increment) {
		RelativeTime changed = fromStart().plusSeconds(increment);
		return from(changed);
	}

	@Override
	public Time startOfDay() {
		return SimpleTime.ofDays(fromStart().toDays());
	}

	@Override
	public Time newTime(int hours, int minutes, int seconds) {
		verify(hours, minutes, seconds);
		long days = fromStart().toDays();
		return SimpleTime.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
	}

	private void verify(int hours, int minutes, int seconds) {
		if (0 > hours || 28 <= hours) {
			throw new IllegalArgumentException("Hours too high or low: " + hours);
		}
		if (0 > minutes || 60 <= minutes) {
			throw new IllegalArgumentException("Minutes too high or low: " + minutes);
		}
		if (0 > seconds || 60 <= seconds) {
			throw new IllegalArgumentException("Seconds too high or low: " + seconds);
		}
	}

	public RelativeTime differenceTo(Time otherDate) {
		return this.fromStart().minus(otherDate.fromStart());
	}

	@Override
	public RelativeTime fromStart() {
		return RelativeTime.ofSeconds(seconds);
	}

	public Time plus(int amount, ChronoUnit unit) {
		return plus(RelativeTime.of(amount, unit));
	}

	public static List<Time> oneWeek() {
		Time start = new SimpleTime();
		List<Time> week = new ArrayList<>();
		for (int day = 0; day < 7; day++) {
			week.add(start.plusDays(day));
		}
		return week;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (seconds ^ (seconds >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleTime other = (SimpleTime) obj;
		if (seconds != other.seconds)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new DateFormat().asWeekdayTime(this);
	}

	@Override
	public int compareTo(Time other) {
		if (isBefore(other)) {
			return -1;
		} else if (other.isBefore(this)) {
			return 1;
		}
		return 0;
	}

}
