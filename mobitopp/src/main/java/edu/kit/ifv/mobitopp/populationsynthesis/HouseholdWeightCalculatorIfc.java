package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

interface HouseholdWeightCalculatorIfc {

	public List<HouseholdOfPanelDataId> calculateWeights(
  	HouseholdDistribution hhDistribution,
  	EmploymentDistribution empDistribution,
  	ContinuousDistributionIfc maleAgeDistribution,
  	ContinuousDistributionIfc femaleAgeDistribution,
  	List<HouseholdOfPanelDataId> householdOfPanelDataIds,
		Map<HouseholdOfPanelDataId,HouseholdOfPanelData> households,
		Map<HouseholdOfPanelDataId,List<PersonOfPanelData>> personsOfHousehold
	);


}
