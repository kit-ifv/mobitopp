package edu.kit.ifv.mobitopp.data;

import java.util.List;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public interface PanelDataRepository {

	List<PersonOfPanelData> getPersonsOfHousehold(HouseholdOfPanelDataId id);

	List<HouseholdOfPanelDataId> getHouseholdIds(int domCodeType);

	HouseholdOfPanelData getHousehold(HouseholdOfPanelDataId id);

}