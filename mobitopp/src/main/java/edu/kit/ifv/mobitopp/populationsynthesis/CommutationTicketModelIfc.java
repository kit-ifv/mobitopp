package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public interface CommutationTicketModelIfc {

	boolean estimateCommutationTicket(
		PersonOfPanelData person,
		HouseholdOfPanelData household,
		Zone zone
	);
	

}
