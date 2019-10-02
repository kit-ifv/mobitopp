package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.simulation.DefaultHouseholdForSetup;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DefaultHouseholdCreator implements HouseholdCreator {

	private final HouseholdLocationSelector householdLocationSelector;
	private final EconomicalStatusCalculator economicalStatusCalculator;
	private final ChargePrivatelySelector canChargePrivatelySelector;
	private int oids;

	public DefaultHouseholdCreator(
			HouseholdLocationSelector householdLocationSelector,
			EconomicalStatusCalculator economicalStatusCalculator,
			ChargePrivatelySelector canChargePrivatelySelector) {
		super();
		this.householdLocationSelector = householdLocationSelector;
		this.economicalStatusCalculator = economicalStatusCalculator;
		this.canChargePrivatelySelector = canChargePrivatelySelector;
		oids = 0;
	}

	@Override
	public HouseholdForSetup createHousehold(HouseholdOfPanelData household, Zone zone) {
		int oid = oids++;
		HouseholdId id = new HouseholdId(oid, household.id().getYear(),
				household.id().getHouseholdNumber());
		int nominalSize = household.size();
		int domcode = household.domCode();
		Location location = householdLocationSelector.selectLocation(zone);
		int numberOfMinors = household.numberOfMinors();
		int numberOfNotSimulatedChildren = household.numberOfNotReportingChildren();
		int totalNumberOfCars = household.numberOfCars();
		int income = household.income();
		int incomeClass = household.incomeClass();
		EconomicalStatus economicalStatus = economicalStatusCalculator
				.calculateFor(nominalSize, numberOfMinors, income);
		boolean canChargePrivately = canChargePrivatelySelector.canChargeAt(zone);
		return new DefaultHouseholdForSetup(id, nominalSize, domcode, zone, location, numberOfMinors,
				numberOfNotSimulatedChildren, totalNumberOfCars, income, incomeClass, economicalStatus,
				canChargePrivately);
	}

}
