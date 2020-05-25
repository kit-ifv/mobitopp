package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute.householdSize;
import static edu.kit.ifv.mobitopp.populationsynthesis.ipu.TransferHouseholds.defaultWeight;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.dataimport.Bbsr17;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class TransferHouseholdsTest {

	private static final int defaultHouseholdSize = 1;
	private static final int defaultHouseholdType = 0;
	private static final RegionalContext context = new DefaultRegionalContext(RegionalLevel.community,
			"1");

  @Test
  public void transfersHouseholdsFromPanelToWeighted() {
    Bbsr17 areaType = Bbsr17.defaultType;
    HouseholdOfPanelData household = createHousehold(areaType);
    List<WeightedHousehold> households = transferHousehold(areaType, household);

    WeightedHousehold weightedHousehold = newWeightedHousehold(household);
    assertThat(households, contains(weightedHousehold));
  }

  @Test
  public void filterHouseholdsByAreaType() {
    Bbsr17 otherAreaType = Bbsr17.defaultType;
    Bbsr17 zoneAreaType = Bbsr17.agglomeratedCountiesInAgglomerationAreas;
    HouseholdOfPanelData householdCorrectType = createHousehold(zoneAreaType);
    HouseholdOfPanelData householdOtherType = createHousehold(otherAreaType);
    List<WeightedHousehold> households = transferHousehold(zoneAreaType, householdCorrectType,
        householdOtherType);

    assertThat(households, is(contains(newWeightedHousehold(householdCorrectType))));
  }

  @Test
  void lowerAreaTypeConstraintWhenNoHouseholdsAreAvailableForCurrentType() throws Exception {
    Bbsr17 householdAreaType = Bbsr17.defaultType;
    Bbsr17 zoneAreaType = Bbsr17.agglomeratedCountiesInAgglomerationAreas;
    HouseholdOfPanelData household = createHousehold(householdAreaType);
    List<WeightedHousehold> households = transferHousehold(zoneAreaType, household);

    assertThat(households, is(contains(newWeightedHousehold(household))));
  }

  @Test
  void ensureHouseholdsAreAvailableForAllHouseholdTypeElements() throws Exception {
    Bbsr17 householdAreaType = Bbsr17.defaultType;
    Bbsr17 zoneAreaType = Bbsr17.agglomeratedCountiesInAgglomerationAreas;
    HouseholdOfPanelData smallHousehold = createHousehold(householdAreaType, 1);
    HouseholdOfPanelData normalHousehold = createHousehold(zoneAreaType, 2);
    HouseholdOfPanelData bigHousehold = createHousehold(zoneAreaType, 3);
    List<String> requiredHouseholds = requiredSingleAndDoublePersonHousehold();
    List<WeightedHousehold> households = transferHousehold(zoneAreaType, requiredHouseholds,
        smallHousehold, normalHousehold, bigHousehold);

    assertThat(households, is(contains(newWeightedHousehold(smallHousehold),
        newWeightedHousehold(normalHousehold), newWeightedHousehold(bigHousehold))));
  }

	private WeightedHousehold newWeightedHousehold(HouseholdOfPanelData household) {
		return new WeightedHousehold(household.id(), defaultWeight, attributes(household.size()),
				context);
	}

  private List<WeightedHousehold> transferHousehold(
      Bbsr17 zoneAreaType, HouseholdOfPanelData... household) {
    List<String> householdAttributes = requiredSinglePersonHousehold();
    return transferHousehold(zoneAreaType, householdAttributes, household);
  }

  private List<String> requiredSingleAndDoublePersonHousehold() {
    return asList(householdSize.createInstanceName(1, 1), householdSize.createInstanceName(2, 2));
  }

  private List<String> requiredSinglePersonHousehold() {
    return asList(householdSize.createInstanceName(1, 1));
  }

  private List<WeightedHousehold> transferHousehold(
      Bbsr17 zoneAreaType, List<String> householdAttributes, HouseholdOfPanelData... household) {
    PanelDataRepository panel = createPanelRepsitory(household);
    AttributeResolver attributeResolver = createAttributeResolver(household);

    TransferHouseholds transfer = new TransferHouseholds(panel, attributeResolver,
        householdAttributes, context);

    return transfer.forAreaType(zoneAreaType);
  }

  private HouseholdOfPanelData createHousehold(Bbsr17 areaType) {
    return createHousehold(areaType, defaultHouseholdSize);
  }

	private HouseholdOfPanelData createHousehold(Bbsr17 areaType, int size) {
		short year = 2011;
		HouseholdOfPanelDataId id = new HouseholdOfPanelDataId(year, 1);
		return new HouseholdOfPanelData(id, areaType.getTypeAsInt(), size, defaultHouseholdType, 0, 0, 0, 0, 0, 0, 0,
				0.0f, 0);
	}

  private PanelDataRepository createPanelRepsitory(HouseholdOfPanelData... household) {
    PanelDataRepository panel = mock(PanelDataRepository.class);
    when(panel.getHouseholds()).thenReturn(asList(household));
    return panel;
  }

  private AttributeResolver createAttributeResolver(HouseholdOfPanelData... households) {
    AttributeResolver attributeResolver = mock(AttributeResolver.class);
    for (HouseholdOfPanelData household : households) {
      when(attributeResolver.attributesOf(household)).thenReturn(attributes(household.size()));
    }
    return attributeResolver;
  }

  private Map<String, Integer> attributes(int size) {
    HashMap<String, Integer> attributes = new HashMap<>();
    attributes.put(householdSize.createInstanceName(size, size), size);
    attributes.put("age:f:20-30", size);
    return attributes;
  }
}
