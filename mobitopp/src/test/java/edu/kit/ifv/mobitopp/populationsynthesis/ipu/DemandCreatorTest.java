package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder.householdOfPanelData;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.demand.DefaultDistributions;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

@ExtendWith(MockitoExtension.class)
public class DemandCreatorTest {

  private static final StandardAttribute householdFilterType = StandardAttribute.householdSize;
  private HouseholdOfPanelData firstPanelHousehold;
  private HouseholdOfPanelData secondPanelHousehold;
  private HouseholdOfPanelData thirdPanelHousehold;
  private HouseholdForSetup firstHousehold;
  private HouseholdForSetup secondHousehold;
  private HouseholdForSetup thirdHousehold;

  @Mock(lenient = true)
  PanelDataRepository panelData;
  @Mock(lenient = true)
  HouseholdBuilder builder;
  @Mock(lenient = true)
  WeightedHouseholdSelector householdSelector;
  @Mock
  Predicate<HouseholdOfPanelData> filter;
  @Mock
  private DemandZone someZone;

  private WeightedHousehold first;
  private WeightedHousehold second;
  private WeightedHousehold third;

  @BeforeEach
  public void initialise() {
    firstPanelHousehold = ExampleHouseholdOfPanelData.household;
    secondPanelHousehold = ExampleHouseholdOfPanelData.otherHousehold;
    thirdPanelHousehold = createBiggerHousehold();
    when(someZone.zone()).thenReturn(ExampleDemandZones.create().someZone().zone());
    DefaultRegionalContext someRegionalContext = someRegionalContext();
    when(someZone.getRegionalContext()).thenReturn(someRegionalContext);
    firstHousehold = ExampleHousehold.createHousehold(someZone(), firstId());
    secondHousehold = ExampleHousehold.createHousehold(someZone(), secondId());
    thirdHousehold = ExampleHousehold.createHousehold(someZone(), thirdId());

    configureMockObjects();
  }

  private DefaultRegionalContext someRegionalContext() {
    return new DefaultRegionalContext(RegionalLevel.zone, someZone.zone().getId().getExternalId());
  }

  private void configureMockObjects() {
    when(builder.householdFor(firstPanelHousehold)).thenReturn(firstHousehold);
    when(builder.householdFor(secondPanelHousehold)).thenReturn(secondHousehold);
    when(builder.householdFor(thirdPanelHousehold)).thenReturn(thirdHousehold);
    when(panelData.getHousehold(firstId())).thenReturn(firstPanelHousehold);
    when(panelData.getHousehold(secondId())).thenReturn(secondPanelHousehold);
    when(panelData.getHousehold(thirdId())).thenReturn(thirdPanelHousehold);
  }

  private void acceptAllHouseholds() {
    when(filter.test(any())).thenReturn(true);
  }

  private HouseholdOfPanelData createBiggerHousehold() {
    HouseholdOfPanelDataId yetAnotherId = new HouseholdOfPanelDataId(
        ExampleHouseholdOfPanelData.year, 1);
    return householdOfPanelData().withId(yetAnotherId).withSize(2).build();
  }

  @Test
  public void createsEnoughHouseholds() {
    acceptAllHouseholds();
    Demography demography = householdDemographyFor(firstPanelHousehold);
    when(someZone.nominalDemography()).thenReturn(demography);
    List<Attribute> householdAttributes = createAttributes();
    List<WeightedHousehold> households = createWeightedHouseholds();
    List<WeightedHousehold> selectedHouseholds = asList(first);
    int amount = selectedHouseholds.size();
    when(householdSelector.selectFrom(asList(first, second), amount))
        .thenReturn(selectedHouseholds);
    DemandCreator creator = newCreator(householdAttributes);

    List<HouseholdForSetup> newHouseholds = creator.demandFor(households);

    assertThat(newHouseholds).hasSize(amount);
  }

  @Test
  public void createsHouseholdsForEachType() {
    acceptAllHouseholds();
    Demography demography = householdDemographyFor(firstPanelHousehold, secondPanelHousehold,
        thirdPanelHousehold);
    when(someZone.nominalDemography()).thenReturn(demography);
    List<Attribute> householdAttributes = createAttributes();
    List<WeightedHousehold> households = createWeightedHouseholds();
    int amount = 1;
    when(householdSelector.selectFrom(asList(first, second), amount)).thenReturn(asList(first));
    when(householdSelector.selectFrom(eq(asList(third)), anyInt())).thenReturn(asList(third));
    DemandCreator creator = newCreator(householdAttributes);

    List<HouseholdForSetup> newHouseholds = creator.demandFor(households);

    assertThat(newHouseholds).hasSize(amount);
  }

  @Test
  void filterHouseholds() throws Exception {
    when(filter.test(firstPanelHousehold)).thenReturn(false);
    Demography demography = householdDemographyFor(firstPanelHousehold);
    when(someZone.nominalDemography()).thenReturn(demography);
    List<Attribute> householdAttributes = createAttributes();
    List<WeightedHousehold> households = createWeightedHouseholds();
    List<WeightedHousehold> selectedHouseholds = asList(first);
    int amount = selectedHouseholds.size();
    when(householdSelector.selectFrom(asList(first, second), amount))
        .thenReturn(selectedHouseholds);
    DemandCreator creator = newCreator(householdAttributes);

    List<HouseholdForSetup> newHouseholds = creator.demandFor(households);

    assertThat(newHouseholds).isEmpty();
  }

  private DemandCreator newCreator(List<Attribute> householdAttributes) {
    return new DemandCreator(builder, panelData, filter, new StructuralDataHouseholdReproducer(
        someZone, householdFilterType, householdSelector, householdAttributes, panelData));
  }

  private List<Attribute> createAttributes() {
    return householdFilterType
        .createAttributes(someZone.nominalDemography(), someZone.getRegionalContext())
        .collect(toList());
  }

  private Demography householdDemographyFor(HouseholdOfPanelData... panelHousehold) {
    RangeDistributionIfc distribution = new DefaultDistributions().createHousehold();
    for (HouseholdOfPanelData household : panelHousehold) {
      distribution.increment(household.size());
    }
    return new Demography(EmploymentDistribution.createDefault(),
        Map.of(householdFilterType, distribution));
  }

  private Zone someZone() {
    return someZone.zone();
  }

  private List<WeightedHousehold> createWeightedHouseholds() {
    double weight = 0.5d;
    first = new WeightedHousehold(firstId(), weight, someZone.getRegionalContext(),
        firstPanelHousehold);
    second = new WeightedHousehold(secondId(), weight, someZone.getRegionalContext(),
        secondPanelHousehold);
    third = new WeightedHousehold(thirdId(), weight, someZone.getRegionalContext(),
        thirdPanelHousehold);
    return asList(first, second, third);
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
