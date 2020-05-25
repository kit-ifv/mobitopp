package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder.householdOfPanelData;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
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
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

@ExtendWith(MockitoExtension.class)
public class DemandCreatorTest {

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
	private WeightedHousehold first;
	private WeightedHousehold second;
	private WeightedHousehold third;
	private List<Attribute> householdAttributes;
	private DemandZone someZone;

	@BeforeEach
	public void initialise() {
		firstPanelHousehold = ExampleHouseholdOfPanelData.household;
		secondPanelHousehold = ExampleHouseholdOfPanelData.otherHousehold;
		thirdPanelHousehold = createBiggerHousehold();
		firstHousehold = ExampleHousehold.createHousehold(dummyZone(), firstId());
		secondHousehold = ExampleHousehold.createHousehold(dummyZone(), secondId());
		thirdHousehold = ExampleHousehold.createHousehold(dummyZone(), thirdId());
		someZone = ExampleDemandZones.create().someZone();
		householdAttributes = StandardAttribute.householdSize
				.createAttributes(someZone.nominalDemography(), someZone.getRegionalContext())
				.collect(toList());

		configureMockObjects();
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
		RangeDistributionIfc distribution = householdDistributionFor(firstPanelHousehold);
		List<WeightedHousehold> households = createWeightedHouseholds();
		List<WeightedHousehold> selectedHouseholds = asList(first);
		int amount = selectedHouseholds.size();
		when(householdSelector.selectFrom(asList(first, second), amount))
				.thenReturn(selectedHouseholds);
		DemandCreator creator = newCreator();

		List<HouseholdForSetup> newHouseholds = creator.demandFor(households, distribution);

		assertThat(newHouseholds).hasSize(amount);
	}

	@Test
	public void createsHouseholdsForEachType() {
		acceptAllHouseholds();
		RangeDistributionIfc distribution = householdDistributionFor(firstPanelHousehold,
				secondPanelHousehold, thirdPanelHousehold);
		List<WeightedHousehold> households = createWeightedHouseholds();
		int amount = 1;
		when(householdSelector.selectFrom(asList(first, second), amount)).thenReturn(asList(first));
		when(householdSelector.selectFrom(eq(asList(third)), anyInt())).thenReturn(asList(third));
		DemandCreator creator = newCreator();

		List<HouseholdForSetup> newHouseholds = creator.demandFor(households, distribution);

		assertThat(newHouseholds).hasSize(amount);
	}

	@Test
	void filterHouseholds() throws Exception {
		when(filter.test(firstPanelHousehold)).thenReturn(false);
		RangeDistributionIfc distribution = householdDistributionFor(firstPanelHousehold);
		List<WeightedHousehold> households = createWeightedHouseholds();
		List<WeightedHousehold> selectedHouseholds = asList(first);
		int amount = selectedHouseholds.size();
		when(householdSelector.selectFrom(asList(first, second), amount))
				.thenReturn(selectedHouseholds);
		DemandCreator creator = newCreator();

		List<HouseholdForSetup> newHouseholds = creator.demandFor(households, distribution);

		assertThat(newHouseholds).isEmpty();
	}

	private DemandCreator newCreator() {
		return new DemandCreator(builder, panelData, householdSelector, householdAttributes, filter);
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
		first = new WeightedHousehold(firstId(), weight, attributes(firstPanelHousehold),
				someZone.getRegionalContext());
		second = new WeightedHousehold(secondId(), weight, attributes(secondPanelHousehold),
				someZone.getRegionalContext());
		third = new WeightedHousehold(thirdId(), weight, attributes(thirdPanelHousehold),
				someZone.getRegionalContext());
		return asList(first, second, third);
	}

	private Map<String, Integer> attributes(HouseholdOfPanelData panelHousehold) {
		return householdAttributes
				.stream()
				.collect(
						toMap(Attribute::name, attribute -> attribute.valueFor(panelHousehold, panelData)));
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
