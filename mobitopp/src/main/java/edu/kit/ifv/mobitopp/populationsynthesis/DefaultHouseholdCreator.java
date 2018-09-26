package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.simulation.HouseholdForDemand;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DefaultHouseholdCreator implements HouseholdCreator {

	private final HouseholdLocationSelector householdLocationSelector;
	private final ChargePrivatelySelector canChargePrivatelySelector;
	private int oids;

	public DefaultHouseholdCreator(
			HouseholdLocationSelector householdLocationSelector,
			ChargePrivatelySelector canChargePrivatelySelector) {
		super();
		this.householdLocationSelector = householdLocationSelector;
		this.canChargePrivatelySelector = canChargePrivatelySelector;
		oids = 0;
	}

	@Override
	public HouseholdForDemand createHousehold(HouseholdOfPanelData household, Zone zone) {
		int oid = oids++;
		HouseholdId id = new HouseholdId(household.id().getYear(), household.id().getHouseholdNumber());
		int nominalSize = household.size();
		int domcode = household.domCode();
		Location location = householdLocationSelector.selectLocation(zone);
		int numberOfNotSimulatedChildren = household.numberOfNotReportingChildren();
		int totalNumberOfCars = household.numberOfCars();
		int income = household.income();
		boolean canChargePrivately = canChargePrivatelySelector.canChargeAt(zone);
		return new HouseholdForDemand(oid, id, nominalSize, domcode, zone, location,
				numberOfNotSimulatedChildren, totalNumberOfCars, income, canChargePrivately);
	}

}
