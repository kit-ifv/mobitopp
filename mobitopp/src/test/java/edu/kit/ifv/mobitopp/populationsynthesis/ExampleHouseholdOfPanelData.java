package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder.householdOfPanelData;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class ExampleHouseholdOfPanelData {

  public static final short year = 2000;
  public static final int aNumber = 3;
  public static final int otherNumber = 4;
  public static final HouseholdOfPanelDataId anId = new HouseholdOfPanelDataId(year, aNumber);
  public static final HouseholdOfPanelDataId otherId = new HouseholdOfPanelDataId(year,
      otherNumber);
  public static final int aDomCode = 6;
  public static final int otherDomCode = 7;
  public static final HouseholdOfPanelData household = household(aDomCode, anId);
  public static final HouseholdOfPanelData otherHousehold = household(aDomCode, otherId);

  public static HouseholdOfPanelData household(int domCode, HouseholdOfPanelDataId id) {
    return householdOfPanelData().withId(id).withDomCode(domCode).build();
  }

}
