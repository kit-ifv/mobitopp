package edu.kit.ifv.mobitopp.data.local.configuration;

import static java.util.Comparator.comparing;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.time.Time;

public class DayTypeMatrices {

	private Map<TimeSpan, String> paths;

	public DayTypeMatrices() {
		super();
		paths = new TreeMap<>(comparing(TimeSpan::getFrom).thenComparing(TimeSpan::getTo));
	}

	public Map<TimeSpan, String> getBetween() {
		return paths;
	}

	public void setBetween(Map<TimeSpan, String> forTime) {
		this.paths = forTime;
	}

	public TimeSpan matchingTimeSpan(Time date) {
		for (TimeSpan timeSpan : paths.keySet()) {
			if (timeSpan.matches(date)) {
				return timeSpan;
			}
		}
		throw new IllegalArgumentException("Cannot match date to time span:" + date);
	}

	public void add(TimeSpan timeSpan, String path) {
		paths.put(timeSpan, path);
	}

	public StoredMatrix in(TimeSpan timeSpan) {
		if (hasMatching(timeSpan)) {
			TimeSpan storedSpan = stored(timeSpan);
			return new StoredMatrix(storedSpan, paths.get(storedSpan));
		}
		throw new IllegalArgumentException("No matrix available for timespan: " + timeSpan);
	}

	private TimeSpan stored(TimeSpan timeSpan) {
		return paths
				.keySet()
				.stream()
				.filter(storedSpan -> storedSpan.contains(timeSpan))
				.findFirst()
				.get();
	}

	private boolean hasMatching(TimeSpan timeSpan) {
		return paths.keySet().stream().anyMatch(storedSpan -> storedSpan.contains(timeSpan));
	}

	public TimeSpan coveredTime() {
		Iterator<TimeSpan> iterator = paths.keySet().iterator();
		TimeSpan covered = iterator.next();
		while (iterator.hasNext()) {
			TimeSpan timeSpan = iterator.next();
			covered = covered.add(timeSpan);
		}
		return covered;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((paths == null) ? 0 : paths.hashCode());
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
		DayTypeMatrices other = (DayTypeMatrices) obj;
		if (paths == null) {
			if (other.paths != null)
				return false;
		} else if (!paths.equals(other.paths))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DayTypeMatrices [forTime=" + paths + "]";
	}

}
