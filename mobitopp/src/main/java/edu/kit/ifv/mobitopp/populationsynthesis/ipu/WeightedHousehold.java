package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.Map;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class WeightedHousehold {

	private final HouseholdOfPanelDataId id;
	private final double weight;
	private final Map<String, Integer> householdAttributes;
	private final Map<String, Integer> personAttributes;

	public WeightedHousehold(
			HouseholdOfPanelDataId id, double weight, Map<String, Integer> householdAttributes,
			Map<String, Integer> personAttributes) {
		super();
		this.id = id;
		this.weight = weight;
		this.householdAttributes = householdAttributes;
		this.personAttributes = personAttributes;
	}

	public HouseholdOfPanelDataId id() {
		return id;
	}

	public double weight() {
		return weight;
	}

	public int householdAttribute(String attribute) {
		return householdAttributes.get(attribute);
	}

	public int personAttribute(String attribute) {
		return personAttributes.get(attribute);
	}

	public WeightedHousehold newWeight(double newWeight) {
		return new WeightedHousehold(id, newWeight, householdAttributes, personAttributes);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((householdAttributes == null) ? 0 : householdAttributes.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((personAttributes == null) ? 0 : personAttributes.hashCode());
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
		if (householdAttributes == null) {
			if (other.householdAttributes != null)
				return false;
		} else if (!householdAttributes.equals(other.householdAttributes))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (personAttributes == null) {
			if (other.personAttributes != null)
				return false;
		} else if (!personAttributes.equals(other.personAttributes))
			return false;
		if (Double.doubleToLongBits(weight) != Double.doubleToLongBits(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Household [id=" + id + ", weight=" + weight + ", householdAttributes="
				+ householdAttributes + ", personAttributes=" + personAttributes + "]";
	}

}
