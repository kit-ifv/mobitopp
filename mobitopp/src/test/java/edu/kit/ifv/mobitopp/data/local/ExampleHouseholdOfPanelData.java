package edu.kit.ifv.mobitopp.data.local;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class ExampleHouseholdOfPanelData {

	public static final short year = 2000;
	public static final int aNumber = 3;
	public static final int otherNumber = 4;
	public static final HouseholdOfPanelDataId anId = new HouseholdOfPanelDataId(year, aNumber);
	public static final HouseholdOfPanelDataId otherId = new HouseholdOfPanelDataId(year, otherNumber);
	public static final int areaType = 4;
	public static final int size = 1;
	private static final int type = 0;
	public static final int aDomCode = 0;
	public static final int otherDomCode = 7;
	public static final int reportingPersons = size;
	public static final int minors = 0;
	public static final int notReportingChildren = 0;
	public static final int cars = 11;
	public static final int income = 12;
  public static final int incomeClass = 1;
  public static final float activityRadius = 13.0f;
  public static final int activityRadiusMode = StandardMode.CAR.getCode();
	public static final HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household(aDomCode, anId);
  public static final float defaultWeight = 1.0f;

  public static HouseholdOfPanelData household(int domCode, HouseholdOfPanelDataId id) {
    return new HouseholdOfPanelData(id, areaType, size, type, domCode, reportingPersons, minors,
        notReportingChildren, cars, income, incomeClass, activityRadius, activityRadiusMode,
        defaultWeight);
  }

}
