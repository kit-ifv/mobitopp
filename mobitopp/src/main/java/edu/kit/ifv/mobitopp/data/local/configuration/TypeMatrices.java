package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DayType;

public class TypeMatrices {

	private static final DayType defaultType = DayType.weekdays;
	
	private Map<DayType, DayTypeMatrices> at;

	public TypeMatrices() {
		super();
		at = new HashMap<>();
	}

	public Map<DayType, DayTypeMatrices> getAt() {
		return at;
	}

	public void setAt(Map<DayType, DayTypeMatrices> at) {
		this.at = at;
	}

	public DayTypeMatrices at(DayType dayType) {
		if (at.containsKey(dayType)) {
			return at.get(dayType);
		}
		return defaultMatrices();
	}

	private DayTypeMatrices defaultMatrices() {
		if (at.containsKey(defaultType)) {
			return at.get(defaultType);
		}
		throw new IllegalStateException(
				"No default matrices are available. Current default type is " + defaultType);
	}

	public void add(DayType dayType, TimeSpan timeSpan, String path) {
		DayTypeMatrices dayTypeMatrices = at.getOrDefault(dayType, new DayTypeMatrices());
		dayTypeMatrices.add(timeSpan, path);
		at.put(dayType, dayTypeMatrices);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((at == null) ? 0 : at.hashCode());
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
		TypeMatrices other = (TypeMatrices) obj;
		if (at == null) {
			if (other.at != null)
				return false;
		} else if (!at.equals(other.at))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [at=" + at + "]";
	}

}
