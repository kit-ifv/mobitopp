package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute.householdSize;
import static edu.kit.ifv.mobitopp.populationsynthesis.ipu.TransferHouseholds.defaultWeight;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.dataimport.Bbsr17;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class TransferHouseholdsTest {

  private static final float defaultHouseholdWeight = 1.0f;
  private static final int defaultHouseholdSize = 1;
  private static final int defaultHouseholdType = 0;
  private static final RegionalContext context = new DefaultRegionalContext(RegionalLevel.community,
      "1");

  @Test
  public void transfersHouseholdsFromPanelToWeighted() {
    Bbsr17 areaType = Bbsr17.defaultType;
    float customWeight = 2.0f;
    HouseholdOfPanelData household = createHousehold()
        .withAreaType(areaType)
        .withWeight(customWeight)
        .build();
    List<WeightedHousehold> households = transferHousehold(areaType, household);

    WeightedHousehold expectedHousehold = newWeightedHousehold(household);
    expectedHousehold.setWeight(customWeight);
    assertThat(households).contains(expectedHousehold);
    WeightedHousehold weightedHousehold = households.get(0);
    assertThat(weightedHousehold.weight()).isEqualTo(customWeight, Offset.offset(1e-6d));
  }

  @Test
  public void filterHouseholdsByAreaType() {
    Bbsr17 otherAreaType = Bbsr17.defaultType;
    Bbsr17 zoneAreaType = Bbsr17.agglomeratedCountiesInAgglomerationAreas;
    HouseholdOfPanelData householdCorrectType = createHousehold()
        .withAreaType(zoneAreaType)
        .build();
    HouseholdOfPanelData householdOtherType = createHousehold().withAreaType(otherAreaType).build();
    List<WeightedHousehold> households = transferHousehold(zoneAreaType, householdCorrectType,
        householdOtherType);

    assertThat(households).contains(newWeightedHousehold(householdCorrectType));
  }

  @Test
  void lowerAreaTypeConstraintWhenNoHouseholdsAreAvailableForCurrentType() throws Exception {
    Bbsr17 householdAreaType = Bbsr17.defaultType;
    Bbsr17 zoneAreaType = Bbsr17.agglomeratedCountiesInAgglomerationAreas;
    HouseholdOfPanelData household = createHousehold().withAreaType(householdAreaType).build();
    List<WeightedHousehold> households = transferHousehold(zoneAreaType, household);

    assertThat(households).contains(newWeightedHousehold(household));
  }

  @Test
  void ensureHouseholdsAreAvailableForAllHouseholdTypeElements() throws Exception {
    Bbsr17 householdAreaType = Bbsr17.defaultType;
    Bbsr17 zoneAreaType = Bbsr17.agglomeratedCountiesInAgglomerationAreas;
    HouseholdOfPanelData smallHousehold = createHousehold()
        .withAreaType(householdAreaType)
        .withSize(1)
        .build();
    HouseholdOfPanelData normalHousehold = createHousehold()
        .withAreaType(zoneAreaType)
        .withSize(2)
        .build();
    HouseholdOfPanelData bigHousehold = createHousehold()
        .withAreaType(zoneAreaType)
        .withSize(3)
        .build();
    List<Attribute> requiredHouseholds = requiredSingleAndDoublePersonHousehold();
    List<WeightedHousehold> households = transferHousehold(zoneAreaType, requiredHouseholds,
        smallHousehold, normalHousehold, bigHousehold);

    assertThat(households)
        .contains(newWeightedHousehold(smallHousehold), newWeightedHousehold(normalHousehold),
            newWeightedHousehold(bigHousehold));
  }

  private WeightedHousehold newWeightedHousehold(HouseholdOfPanelData household) {
    return new WeightedHousehold(household.id(), defaultWeight, Map.of(), context, household);
  }

  private List<WeightedHousehold> transferHousehold(
      Bbsr17 zoneAreaType, HouseholdOfPanelData... household) {
    List<Attribute> householdAttributes = requiredSinglePersonHousehold();
    return transferHousehold(zoneAreaType, householdAttributes, household);
  }

  private List<Attribute> requiredSingleAndDoublePersonHousehold() {
    Attribute householdSizeOne = createAttributeWithSize(1);
    Attribute householdSizeTwo = createAttributeWithSize(2);
    return asList(householdSizeOne, householdSizeTwo);
  }

  private Attribute createAttributeWithSize(int size) {
    Attribute household = mock(Attribute.class);
    when(household.valueFor(any(), any()))
        .then(invocation -> size == ((HouseholdOfPanelData) invocation.getArgument(0)).size() ? 1
            : 0);
    when(household.type()).thenReturn(householdSize);
    return household;
  }

  private List<Attribute> requiredSinglePersonHousehold() {
    return asList(createAttributeWithSize(1));
  }

  private List<WeightedHousehold> transferHousehold(
      Bbsr17 zoneAreaType, List<Attribute> householdAttributes, HouseholdOfPanelData... household) {
    PanelDataRepository panel = createPanelRepsitory(household);

    TransferHouseholds transfer = new TransferHouseholds(panel, householdAttributes, context);

    return transfer.forAreaType(zoneAreaType);
  }

  private HouseholdOfPanelDataBuilder createHousehold() {
    short year = 2011;
    HouseholdOfPanelDataId id = new HouseholdOfPanelDataId(year, 1);
    return new HouseholdOfPanelDataBuilder()
        .withId(id)
        .withType(defaultHouseholdType)
        .withSize(defaultHouseholdSize)
        .withWeight(defaultHouseholdWeight);
  }

  private PanelDataRepository createPanelRepsitory(HouseholdOfPanelData... household) {
    PanelDataRepository panel = mock(PanelDataRepository.class);
    when(panel.getHouseholds()).thenReturn(asList(household));
    return panel;
  }
}
