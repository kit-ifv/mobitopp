package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public interface PersonCreator {

	public PersonBuilder createPerson(
		PersonOfPanelData panelPerson,
		HouseholdOfPanelData panelHousehold,
		HouseholdForSetup household,
		Zone zone
	);

}

