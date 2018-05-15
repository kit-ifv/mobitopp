package edu.kit.ifv.mobitopp.simulation.person;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class Statistic {

	private final Map<Element, RelativeTime> elements;

	public Statistic() {
		super();
		elements = new HashMap<>();
	}

	public void add(Element element, RelativeTime inVehicleTime) {
		elements.put(element, inVehicleTime);
	}

	public RelativeTime get(Element element) {
		return elements.get(element);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
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
		Statistic other = (Statistic) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Statistic [elements=" + elements + "]";
	}

}
