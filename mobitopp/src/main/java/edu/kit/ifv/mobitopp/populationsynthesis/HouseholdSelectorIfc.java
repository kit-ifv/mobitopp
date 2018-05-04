package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

interface HouseholdSelectorIfc {

	public List<HouseholdOfPanelDataId> selectHouseholds(
		List<HouseholdOfPanelDataId> householdOfPanelDataIds,
		int amount
	);


}
