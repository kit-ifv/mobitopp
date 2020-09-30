package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class WeightedHousehold {

	private static final int missingAttributeValue = 0;
	
  private final HouseholdOfPanelDataId id;
	private double weight;
	private final Map<String, Integer> attributes;
	private final RegionalContext context;
	
	public WeightedHousehold(WeightedHousehold other) {
		this.id = other.id;
		this.weight = other.weight;
		this.attributes = other.attributes;
		this.context = other.context;
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
	
	public Set<String> attributeNames() {
	  return attributes.keySet();
	}
	
	public RegionalContext context() {
		return context;
	}

	public void setWeight(double newWeight) {
		this.weight = newWeight;
	}

}
