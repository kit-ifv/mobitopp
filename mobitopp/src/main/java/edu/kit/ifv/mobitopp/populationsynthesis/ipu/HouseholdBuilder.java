package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public interface HouseholdBuilder {

	HouseholdForSetup householdFor(final HouseholdOfPanelData household, final DemandZone zone);

}