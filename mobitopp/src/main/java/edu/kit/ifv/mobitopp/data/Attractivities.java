package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.simulation.ActivityType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.io.Serializable;

public class Attractivities implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<ActivityType, Integer> items = new LinkedHashMap<ActivityType, Integer>();

	public Attractivities() {
		super();
	}

	public void addAttractivity(ActivityType activityType, int value) {
		this.items.put(activityType, value);
	}

	public Map<ActivityType, Integer> getItems() {
		return this.items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		Attractivities other = (Attractivities) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Attractivities [items=" + items + "]";
	}
	
}
