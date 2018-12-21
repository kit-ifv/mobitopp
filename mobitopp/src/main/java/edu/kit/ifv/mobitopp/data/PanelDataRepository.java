package edu.kit.ifv.mobitopp.data;

import java.util.List;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public interface PanelDataRepository {

	List<PersonOfPanelData> getPersonsOfHousehold(HouseholdOfPanelDataId id);
	
	PersonOfPanelData getPerson(PersonOfPanelDataId personOfPanelDataId);

	List<HouseholdOfPanelDataId> getHouseholdIds(int domCodeType);

	HouseholdOfPanelData getHousehold(HouseholdOfPanelDataId id);

	List<HouseholdOfPanelData> getHouseholds();


}