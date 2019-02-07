package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder.householdOfPanelData;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.demand.DefaultDistributions;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class DemandCreatorTest {

  private HouseholdOfPanelData firstPanelHousehold;
  private HouseholdOfPanelData secondPanelHousehold;
  private HouseholdOfPanelData thirdPanelHousehold;
  private HouseholdForSetup firstHousehold;
  private HouseholdForSetup secondHousehold;
  private HouseholdForSetup thirdHousehold;
  private PanelDataRepository panelData;
  private HouseholdBuilder builder;
  private WeightedHousehold first;
  private WeightedHousehold second;
  private WeightedHousehold third;

  @Before
  public void initialise() {
    firstPanelHousehold = ExampleHouseholdOfPanelData.household;
    secondPanelHousehold = ExampleHouseholdOfPanelData.otherHousehold;
    thirdPanelHousehold = createBiggerHousehold();
    firstHousehold = ExampleHousehold.createHousehold(dummyZone(), firstId());
    secondHousehold = ExampleHousehold.createHousehold(dummyZone(), secondId());
    thirdHousehold = ExampleHousehold.createHousehold(dummyZone(), thirdId());

    createMockObjects();
  }

  private void createMockObjects() {
    builder = mock(HouseholdBuilder.class);
    when(builder.householdFor(firstPanelHousehold)).thenReturn(firstHousehold);
    when(builder.householdFor(secondPanelHousehold)).thenReturn(secondHousehold);
    when(builder.householdFor(thirdPanelHousehold)).thenReturn(thirdHousehold);
    panelData = mock(PanelDataRepository.class);
    when(panelData.getHousehold(firstId())).thenReturn(firstPanelHousehold);
    when(panelData.getHousehold(secondId())).thenReturn(secondPanelHousehold);
    when(panelData.getHousehold(thirdId())).thenReturn(thirdPanelHousehold);
  }

  private HouseholdOfPanelData createBiggerHousehold() {
    HouseholdOfPanelDataId yetAnotherId = new HouseholdOfPanelDataId(
        ExampleHouseholdOfPanelData.year, 1);
    return householdOfPanelData().withId(yetAnotherId).withSize(2).build();
  }

  @Test
  public void createsEnoughHouseholds() {
    RangeDistributionIfc distribution = householdDistributionFor(firstPanelHousehold);
    List<WeightedHousehold> households = createWeightedHouseholds();
    WeightedHouseholdSelector householdSelector = mock(WeightedHouseholdSelector.class);
    List<WeightedHousehold> selectedHouseholds = asList(first);
    int amount = selectedHouseholds.size();
    when(householdSelector.selectFrom(asList(first, second), amount))
        .thenReturn(selectedHouseholds);
    DemandCreator creator = new DemandCreator(builder, panelData, householdSelector,
        StandardAttribute.householdSize);

    List<HouseholdForSetup> newHouseholds = creator.demandFor(households, distribution);

    assertThat(newHouseholds, hasSize(amount));
  }

  @Test
  public void createsHouseholdsForEachType() {
    RangeDistributionIfc distribution = householdDistributionFor(firstPanelHousehold,
        secondPanelHousehold, thirdPanelHousehold);
    List<WeightedHousehold> households = createWeightedHouseholds();
    WeightedHouseholdSelector householdSelector = mock(WeightedHouseholdSelector.class);
    int amount = 1;
    when(householdSelector.selectFrom(asList(first, second), amount)).thenReturn(asList(first));
    when(householdSelector.selectFrom(eq(asList(third)), anyInt())).thenReturn(asList(third));
    DemandCreator creator = new DemandCreator(builder, panelData, householdSelector,
        StandardAttribute.householdSize);

    List<HouseholdForSetup> newHouseholds = creator.demandFor(households, distribution);

    assertThat(newHouseholds, hasSize(amount));
  }

  private RangeDistributionIfc householdDistributionFor(HouseholdOfPanelData... panelHousehold) {
    RangeDistributionIfc distribution = new DefaultDistributions().createHousehold();
    for (HouseholdOfPanelData household : panelHousehold) {
      distribution.increment(household.size());
    }
    return distribution;
  }

  private Zone dummyZone() {
    return ExampleDemandZones.create().someZone().zone();
  }

  private List<WeightedHousehold> createWeightedHouseholds() {
    double weight = 0.5d;
    first = new WeightedHousehold(firstId(), weight, attributes(firstPanelHousehold));
    second = new WeightedHousehold(secondId(), weight, attributes(secondPanelHousehold));
    third = new WeightedHousehold(thirdId(), weight, attributes(thirdPanelHousehold));
    return asList(first, second, third);
  }

  private Map<String, Integer> attributes(HouseholdOfPanelData panelHousehold) {
    String type = StandardAttribute.householdSize
        .createInstanceName(panelHousehold.size(), panelHousehold.size());
    return singletonMap(type, 1);
  }

  private HouseholdOfPanelDataId secondId() {
    return secondPanelHousehold.id();
  }

  private HouseholdOfPanelDataId firstId() {
    return firstPanelHousehold.id();
  }

  private HouseholdOfPanelDataId thirdId() {
    return thirdPanelHousehold.id();
  }
}
