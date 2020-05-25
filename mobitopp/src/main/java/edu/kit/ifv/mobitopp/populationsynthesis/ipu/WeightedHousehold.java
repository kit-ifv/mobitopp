package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.Map;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class WeightedHousehold {

	private static final int missingAttributeValue = 0;
	
  private final HouseholdOfPanelDataId id;
	private final double weight;
	private final Map<String, Integer> attributes;
	private final RegionalContext context;

	public HouseholdOfPanelDataId id() {
		return id;
	}

	public double weight() {
		return weight;
	}

	public int attribute(String attribute) {
		return attributes.getOrDefault(attribute, missingAttributeValue);
	}
	
	public RegionalContext context() {
		return context;
	}

	public WeightedHousehold newWeight(double newWeight) {
		return new WeightedHousehold(id, newWeight, attributes, context);
	}

}
