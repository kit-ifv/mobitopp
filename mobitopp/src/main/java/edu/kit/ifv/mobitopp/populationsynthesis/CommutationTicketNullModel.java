package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

class CommutationTicketNullModel 
	implements CommutationTicketModelIfc {

	public boolean estimateCommutationTicket(
		PersonOfPanelData person,
		HouseholdOfPanelData household,
		Zone zone
	) {

		return person.hasCommuterTicket();
	}
	

}
