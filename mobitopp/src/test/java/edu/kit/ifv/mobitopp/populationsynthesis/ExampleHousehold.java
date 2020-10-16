package edu.kit.ifv.mobitopp.populationsynthesis;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.simulation.DefaultHouseholdForSetup;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class ExampleHousehold {

	static final int oid = 1;
	static final short year = 2000;
	public static final HouseholdId id = new HouseholdId(oid, year, 3);
	static final int defaultNominalSize = 2;
	static final int domcode = 5;
	static final int type = 6;
	static final Location location = new Location(new Point2D.Double(), 0, 0.0d);
	static final int numberOfMinors = 0;
	static final int numberOfNotSimulatedChildren = 0;
	static final int defaultTotalNumberOfCars = 0;
	static final int income = 8;
	static final int incomeClass = 1;
	static final EconomicalStatus economicalStatus = EconomicalStatus.veryLow;
	static final boolean canChargePrivately = false;

	private final Zone zone;
	private int nominalSize;
	private int numberOfCars;

	public ExampleHousehold(Zone zone) {
		this.zone = zone;
		nominalSize = defaultNominalSize;
		numberOfCars = defaultTotalNumberOfCars;
	}

	public static HouseholdForSetup createHousehold(Zone zone) {
		return new DefaultHouseholdForSetup(id, defaultNominalSize, domcode, type, zone, location,
				numberOfMinors, numberOfNotSimulatedChildren, defaultTotalNumberOfCars, income, incomeClass,
				economicalStatus, canChargePrivately);
	}

	public static HouseholdForSetup createHousehold(Zone zone, HouseholdOfPanelDataId panelId) {
		HouseholdId id = new HouseholdId(oid, panelId.getYear(), panelId.getHouseholdNumber());
		return new DefaultHouseholdForSetup(id, defaultNominalSize, domcode, type, zone, location,
				numberOfMinors, numberOfNotSimulatedChildren, defaultTotalNumberOfCars, income, incomeClass,
				economicalStatus, canChargePrivately);
	}

	public ExampleHousehold withSize(int nominalSize) {
		this.nominalSize = nominalSize;
		return this;
	}

	public ExampleHousehold withCars(int numberOfCars) {
		this.numberOfCars = numberOfCars;
		return this;
	}

	public HouseholdForSetup build() {
		return new DefaultHouseholdForSetup(id, nominalSize, domcode, type, zone, location, numberOfMinors,
				numberOfNotSimulatedChildren, numberOfCars, income, incomeClass, economicalStatus,
				canChargePrivately);
	}

}
