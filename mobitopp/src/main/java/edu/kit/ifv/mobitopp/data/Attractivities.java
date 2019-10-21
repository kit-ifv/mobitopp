package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;
import java.io.Serializable;

@EqualsAndHashCode
@ToString
public class Attractivities implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<ActivityType, Integer> items = new LinkedHashMap<>();

	public Attractivities() {
		super();
	}

	public void addAttractivity(ActivityType activityType, int value) {
		this.items.put(activityType, value);
	}

	public Map<ActivityType, Integer> getItems() {
		return this.items;
	}
	
}
