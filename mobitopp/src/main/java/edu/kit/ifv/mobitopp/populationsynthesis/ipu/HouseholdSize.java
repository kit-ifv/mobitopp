package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Constraint;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.HouseholdConstraint;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class HouseholdSize implements Attribute {

	private final String prefix;
	private final int value;

	public HouseholdSize(String prefix, int value) {
		this.prefix = prefix;
		this.value = value;
	}

	@Override
	public int valueFor(HouseholdOfPanelData household, PanelDataRepository panelDataRepository) {
		return household.size() == value ? 1 : 0;
	}

	@Override
	public String name() {
		return prefix + value;
	}

	@Override
	public Constraint createConstraint(Demography demography) {
		HouseholdDistributionItem item = demography.household().getItem(value);
		int requestedWeight = item.amount();
		return new HouseholdConstraint(requestedWeight, name());
	}

}
