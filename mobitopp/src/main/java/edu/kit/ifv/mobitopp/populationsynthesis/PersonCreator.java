package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public interface PersonCreator {

	public Person createPerson(
		PersonOfPanelData panelPerson,
		HouseholdOfPanelData panelHousehold,
		Household household,
		Zone zone
	);

}

