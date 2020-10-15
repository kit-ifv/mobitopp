package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

@ExtendWith(MockitoExtension.class)
public class WeightedHouseholdReproducerTest {

  private static final RegionalContext context = new DefaultRegionalContext(RegionalLevel.zone,
      "1");

  @Mock
  private Attribute someAttribute;
  @Mock
  private Attribute otherAttribute;
  @Mock
  private WeightedHouseholdSelector householdSelector;
  @Mock
  private PanelDataRepository panelData;

  @Test
  void multipliesHouseholdsByWeight() throws Exception {
    when(someAttribute.valueFor(any(), any())).thenReturn(1);
    int amount = 3;
    double weight = amount / 2.0d;
    WeightedHousehold someHousehold = createHousehold(weight, 1);
    WeightedHousehold otherHousehold = createHousehold(weight, 2);
    List<WeightedHousehold> selectedHouseholds = List
        .of(someHousehold, otherHousehold, someHousehold);
    List<WeightedHousehold> households = List.of(someHousehold, otherHousehold);
    when(householdSelector.selectFrom(households, amount)).thenReturn(selectedHouseholds);
    List<Attribute> householdAttributes = List.of(someAttribute);

    WeightedHouseholdReproducer reproducer = new WeightedHouseholdReproducer(householdAttributes,
        householdSelector, panelData);
    Stream<WeightedHousehold> moreHouseholds = reproducer.getHouseholdsToCreate(households);

    assertThat(moreHouseholds).containsExactlyElementsOf(selectedHouseholds);
    verify(householdSelector).selectFrom(households, amount);
  }

  @Test
  void multipliesHouseholdsPerAttributeByWeight() throws Exception {
    int amount = 3;
    double weight = amount / 2.0d;
    WeightedHousehold someHousehold = createHousehold(weight, 1);
    WeightedHousehold ansomeHousehold = createHousehold(weight, 2);
    WeightedHousehold otherHousehold = createHousehold(weight, 3);
    WeightedHousehold anotherHousehold = createHousehold(weight, 4);
    when(someAttribute.valueFor(someHousehold.household(), panelData)).thenReturn(1);
    when(someAttribute.valueFor(ansomeHousehold.household(), panelData)).thenReturn(1);
    when(someAttribute.valueFor(otherHousehold.household(), panelData)).thenReturn(0);
    when(someAttribute.valueFor(anotherHousehold.household(), panelData)).thenReturn(0);
    when(otherAttribute.valueFor(otherHousehold.household(), panelData)).thenReturn(1);
    when(otherAttribute.valueFor(anotherHousehold.household(), panelData)).thenReturn(1);
    when(otherAttribute.valueFor(someHousehold.household(), panelData)).thenReturn(0);
    when(otherAttribute.valueFor(ansomeHousehold.household(), panelData)).thenReturn(0);
    List<WeightedHousehold> someHouseholds = List.of(someHousehold, ansomeHousehold);
    List<WeightedHousehold> otherHouseholds = List.of(otherHousehold, anotherHousehold);
    List<WeightedHousehold> someSelectedHouseholds = List
        .of(someHousehold, ansomeHousehold, someHousehold);
    List<WeightedHousehold> otherSelectedHouseholds = List
        .of(anotherHousehold, otherHousehold, anotherHousehold);
    when(householdSelector.selectFrom(someHouseholds, amount)).thenReturn(someSelectedHouseholds);
    when(householdSelector.selectFrom(otherHouseholds, amount)).thenReturn(otherSelectedHouseholds);
    List<Attribute> householdAttributes = List.of(someAttribute, otherAttribute);
    List<WeightedHousehold> allHouseholds = List
        .of(someHousehold, ansomeHousehold, otherHousehold, anotherHousehold);

    WeightedHouseholdReproducer reproducer = new WeightedHouseholdReproducer(householdAttributes,
        householdSelector, panelData);
    List<WeightedHousehold> moreHouseholds = reproducer
        .getHouseholdsToCreate(allHouseholds)
        .collect(toList());

    assertThat(moreHouseholds)
        .containsAll(someSelectedHouseholds)
        .containsAll(otherSelectedHouseholds);
    verify(householdSelector).selectFrom(someHouseholds, amount);
  }

  private WeightedHousehold createHousehold(double weight, int number) {
    short year = 2000;
    HouseholdOfPanelDataId id = new HouseholdOfPanelDataId(year, number);
    HouseholdOfPanelData panelHousehold = new HouseholdOfPanelDataBuilder().withId(id).build();
    return new WeightedHousehold(weight, context, panelHousehold);
  }
}
