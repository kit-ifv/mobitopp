package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

interface FixedDestinationSelector {

	public void setFixedDestinations(
  	Zone zone,
  	Map<Integer, Person> persons,
  	Map<Integer, PersonOfPanelData> panelPersons
	);
}
