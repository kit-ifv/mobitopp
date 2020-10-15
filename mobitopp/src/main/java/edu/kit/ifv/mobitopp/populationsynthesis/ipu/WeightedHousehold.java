package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class WeightedHousehold {

	private final double weight;
	private final RegionalContext context;
	private final HouseholdOfPanelData household;
	
	public HouseholdOfPanelDataId id() {
		return household.getId();
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
