package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public interface HouseholdBuilder {

  HouseholdForSetup householdFor(HouseholdOfPanelData panelHousehold);

}