package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class WeightedHousehold {

	private final HouseholdOfPanelDataId id;
	private final double weight;
	private final RegionalContext context;
	private final HouseholdOfPanelData household;
	
	public WeightedHousehold(WeightedHousehold other) {
		this.id = other.id;
		this.weight = other.weight;
		this.context = other.context;
		this.household = other.household;
	}

	public HouseholdOfPanelDataId id() {
		return id;
	}

	public double weight() {
		return weight;
	}

	public RegionalContext context() {
		return context;
	}

  public HouseholdOfPanelData household() {
    return this.household;
  }

}
