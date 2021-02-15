package edu.kit.ifv.mobitopp.data.local.configuration;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeSpan implements Comparable<TimeSpan> {

  private static final Comparator<TimeSpan> comparator = Comparator
      .comparing(TimeSpan::getFrom)
      .thenComparing(TimeSpan::getTo);
  private int from;
	private int to;

	public TimeSpan(int from, int to) {
		super();
		this.from = from;
		this.to = to;
	}

	public TimeSpan(int from) {
		this(from, from);
	}

	/**
	 * From start hour inclusive to end hour inclusive.
	 * 
	 * @param startInclusive
	 * @param endInclusive
	 * @return
	 */
	public static TimeSpan between(int startInclusive, int endInclusive) {
		return new TimeSpan(startInclusive, endInclusive);
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public Stream<TimeSpan> hours() {
		return IntStream.rangeClosed(from, to).mapToObj(TimeSpan::new);
	}

	public boolean matches(Time date) {
		int hour = date.getHour();
		return from <= hour && to >= hour;
	}

	public boolean contains(TimeSpan timeSpan) {
		return from <= timeSpan.from && to >= timeSpan.to;
	}

	public TimeSpan add(TimeSpan other) {
		if (follows(other)) {
			return TimeSpan.between(from, other.to);
		}
		throw warn(new IllegalArgumentException("Time spans do not follow up."), log);
	}

	private boolean follows(TimeSpan other) {
		return startOfNextSpan() == other.from;
	}

	private int startOfNextSpan() {
		return to + 1;
	}
	
	@Override
	public int compareTo(TimeSpan other) {
	  return comparator.compare(this, other);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + from;
		result = prime * result + to;
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
		TimeSpan other = (TimeSpan) obj;
		if (from != other.from)
			return false;
		if (to != other.to)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return from + "-to-" + to;
	}

}
