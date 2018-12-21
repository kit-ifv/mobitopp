package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public interface HouseholdCreator {

	HouseholdForSetup createHousehold(HouseholdOfPanelData household, Zone zone);

}
