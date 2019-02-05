package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.ipu.TransferHouseholds.defaultWeight;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.util.dataimport.Bbsr17;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class TransferHouseholdsTest {

  @Test
  public void transfersHouseholdsFromPanelToWeighted() {
    Bbsr17 areaType = Bbsr17.defaultType;
    HouseholdOfPanelData household = createHousehold(areaType);
    List<WeightedHousehold> households = transferHousehold(areaType, household);

    WeightedHousehold weightedHousehold = new WeightedHousehold(household.id(), defaultWeight,
        attributes());
    assertThat(households, contains(weightedHousehold));
  }
  
  @Test
  public void filterHouseholdsByAreaType() {
    Bbsr17 householdAreaType = Bbsr17.defaultType;
    Bbsr17 zoneAreaType = Bbsr17.agglomeratedCountiesInAgglomerationAreas;
    HouseholdOfPanelData household = createHousehold(householdAreaType);
    List<WeightedHousehold> households = transferHousehold(zoneAreaType, household);

    assertThat(households, is(empty()));
  }

  private List<WeightedHousehold> transferHousehold(
      Bbsr17 zoneAreaType, HouseholdOfPanelData household) {
    PanelDataRepository panel = createPanelRepsitory(household);
    AttributeResolver attributeResolver = createAttributeResolver(household);

    TransferHouseholds transfer = new TransferHouseholds(panel, attributeResolver);

    List<WeightedHousehold> households = transfer.forAreaType(zoneAreaType);
    return households;
  }

  private HouseholdOfPanelData createHousehold(Bbsr17 areaType) {
    short year = 2011;
    HouseholdOfPanelDataId id = new HouseholdOfPanelDataId(year, 1);
    return new HouseholdOfPanelData(id, areaType.getTypeAsInt(), 0, 0, 0, 0, 0, 0, 0);
  }

  private PanelDataRepository createPanelRepsitory(HouseholdOfPanelData household) {
    PanelDataRepository panel = mock(PanelDataRepository.class);
    when(panel.getHouseholds()).thenReturn(asList(household));
    return panel;
  }

  private AttributeResolver createAttributeResolver(HouseholdOfPanelData household) {
    AttributeResolver attributeResolver = mock(AttributeResolver.class);
    when(attributeResolver.attributesOf(household)).thenReturn(attributes());
    return attributeResolver;
  }

  private Map<String, Integer> attributes() {
    HashMap<String, Integer> attributes = new HashMap<>();
    attributes.put("size", 2);
    attributes.put("age:f:20-30", 2);
    return attributes;
  }
}
