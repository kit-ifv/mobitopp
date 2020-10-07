package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class HouseholdOfPanelDataBuilder {

  public static final short defaultYear = 2000;
  public static final int aNumber = 3;
  public static final int otherNumber = 4;
  public static final HouseholdOfPanelDataId anId = new HouseholdOfPanelDataId(defaultYear,
      aNumber);
  public static final HouseholdOfPanelDataId otherId = new HouseholdOfPanelDataId(defaultYear,
      otherNumber);
  public static final int defaultAreaType = 4;
  public static final int defaultSize = 1;
	public static final int type = 0;
  public static final int aDomCode = 6;
  public static final int otherDomCode = 7;
  public static final int defaultReportingPersons = 8;
  public static final int defaultMinors = 9;
  public static final int defaultNotReportingChildren = 10;
  public static final int defaultCars = 11;
  public static final int defaultIncome = 12;
  public static final int defaultIncomeClass = 1;
	public static final float activityRadius = 13.0f;
  public static final int activityRadiusMode = StandardMode.CAR.getCode();
  public static final float defaultWeight = 1.0f;

  private HouseholdOfPanelDataId id;
  private int domCode;
  private int size;

  public HouseholdOfPanelDataBuilder() {
    this.id = anId;
    this.domCode = aDomCode;
    this.size = defaultSize;
  }

  public static HouseholdOfPanelDataBuilder householdOfPanelData() {
    return new HouseholdOfPanelDataBuilder();
  }

  public HouseholdOfPanelDataBuilder withId(HouseholdOfPanelDataId id) {
    this.id = id;
    return this;
  }

  public HouseholdOfPanelDataBuilder withDomCode(int domCode) {
    this.domCode = domCode;
    return this;
  }

  public HouseholdOfPanelDataBuilder withSize(int size) {
    this.size = size;
    return this;
  }

	public HouseholdOfPanelData build() {
		return new HouseholdOfPanelData(id, defaultAreaType, size, type, domCode, defaultReportingPersons,
				defaultMinors, defaultNotReportingChildren, defaultCars, defaultIncome, defaultIncomeClass,
				activityRadius, activityRadiusMode, defaultWeight);
	}

}
