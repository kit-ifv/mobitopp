package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Map;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public interface FixedDestinationSelector {

	public void setFixedDestinations(
  	Zone zone,
  	Map<Integer, PersonBuilder> persons,
  	Map<Integer, PersonOfPanelData> panelPersons
	);
}
