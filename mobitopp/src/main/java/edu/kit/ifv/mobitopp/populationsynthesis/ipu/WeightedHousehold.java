package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.Map;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class WeightedHousehold {

	private static final int missingAttributeValue = 0;
	
  private final HouseholdOfPanelDataId id;
	private final double weight;
	private final Map<String, Integer> attributes;

	public WeightedHousehold(
			HouseholdOfPanelDataId id, double weight, Map<String, Integer> attributes) {
		super();
		this.id = id;
		this.weight = weight;
		this.attributes = attributes;
	}

	public HouseholdOfPanelDataId id() {
		return id;
	}

	public double weight() {
		return weight;
	}

	public int attribute(String attribute) {
		return attributes.getOrDefault(attribute, missingAttributeValue);
	}

	public WeightedHousehold newWeight(double newWeight) {
		return new WeightedHousehold(id, newWeight, attributes);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		WeightedHousehold other = (WeightedHousehold) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(weight) != Double.doubleToLongBits(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WeightedHousehold [id=" + id + ", weight=" + weight + ", attributes=" + attributes
				+ "]";
	}

}
