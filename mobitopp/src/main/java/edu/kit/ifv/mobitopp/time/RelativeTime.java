package edu.kit.ifv.mobitopp.time;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class RelativeTime implements Comparable<RelativeTime> {

	public static final RelativeTime ZERO = RelativeTime.of(0, MINUTES);
	public static final RelativeTime INFINITE = RelativeTime.of(100, DAYS);
	private final Duration duration;

	private RelativeTime(Duration duration) {
		super();
		this.duration = duration;
	}

	public static RelativeTime of(long amount, ChronoUnit unit) {
		return new RelativeTime(Duration.of(amount, unit));
	}

	public long seconds() {
		return duration.getSeconds();
	}

	public long toMinutes() {
		return duration.toMinutes();
	}

	public long toHours() {
		return duration.toHours();
	}

	public long toDays() {
		return duration.toDays();
	}

	public RelativeTime plus(long amount, ChronoUnit unit) {
		return new RelativeTime(duration.plus(amount, unit));
	}

	public RelativeTime plus(RelativeTime other) {
		return new RelativeTime(duration.plus(other.duration));
	}

	public RelativeTime plusHours(long hours) {
		return new RelativeTime(duration.plusHours(hours));
	}

	public RelativeTime plusMinutes(long minutes) {
		return new RelativeTime(duration.plusMinutes(minutes));
	}

	public RelativeTime plusSeconds(long seconds) {
		return new RelativeTime(duration.plusSeconds(seconds));
	}

	public RelativeTime plusDays(long days) {
		return new RelativeTime(duration.plusDays(days));
	}

	public RelativeTime minus(RelativeTime other) {
		return new RelativeTime(duration.minus(other.duration));
	}

	public RelativeTime minusDays(long days) {
		return new RelativeTime(duration.minusDays(days));
	}

	public Duration toDuration() {
		return duration;
	}

	@Override
	public int compareTo(RelativeTime other) {
		return duration.compareTo(other.duration);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RelativeTime other = (RelativeTime) obj;
		if (duration == null) {
			if (other.duration != null) {
				return false;
			}
		} else if (!duration.equals(other.duration)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RelativeTime [duration=" + duration + "]";
	}

	public static RelativeTime ofDays(long days) {
		return new RelativeTime(Duration.ofDays(days));
	}

	public static RelativeTime ofHours(long hour) {
		return new RelativeTime(Duration.ofHours(hour));
	}

	public static RelativeTime ofMinutes(long minutes) {
		return new RelativeTime(Duration.ofMinutes(minutes));
	}

	public static RelativeTime ofSeconds(long seconds) {
		return new RelativeTime(Duration.ofSeconds(seconds));
	}

	public static RelativeTime of(Duration duration) {
		return new RelativeTime(duration);
	}


}
