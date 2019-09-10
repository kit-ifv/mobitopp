package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.data.local.ExamplePersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class HouseholdWeightCalculatorTest {

	private static final int incorrectGender = 3;
	private static final HouseholdOfPanelDataId householdId = ExampleHouseholdOfPanelData.anId;
	private static final HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household;
	private static final PersonOfPanelData person = ExamplePersonOfPanelData.aPerson;

	private HouseholdWeightCalculator calculator;
	private RangeDistributionIfc hhDistribution;
	private EmploymentDistribution empDistribution;
	private RangeDistributionIfc maleAgeDistribution;
	private RangeDistributionIfc femaleAgeDistribution;
	private Map<HouseholdOfPanelDataId, HouseholdOfPanelData> households;

	@Before
	public void initialise() {
		calculator = new HouseholdWeightCalculator();
		hhDistribution = new RangeDistribution();
		hhDistribution.addItem(new RangeDistributionItem(household.domCode(), 1));
		empDistribution = new EmploymentDistribution();
		empDistribution.addItem(new EmploymentDistributionItem(person.employment(), 1));
		maleAgeDistribution = new RangeDistribution();
		maleAgeDistribution.addItem(new RangeDistributionItem(0, Integer.MAX_VALUE, 1));
		femaleAgeDistribution = new RangeDistribution();
		femaleAgeDistribution.addItem(new RangeDistributionItem(0, Integer.MAX_VALUE, 1));
		households = singletonMap(householdId, household);
	}

	private Map<HouseholdOfPanelDataId, List<PersonOfPanelData>> correctPersons() {
		Map<HouseholdOfPanelDataId, List<PersonOfPanelData>> persons = singletonMap(householdId,
				asList(person));
		return persons;
	}

	private void calculateWeightsFor(Map<HouseholdOfPanelDataId, List<PersonOfPanelData>> persons) {
		calculator.calculateWeights(hhDistribution, empDistribution, maleAgeDistribution,
				femaleAgeDistribution, householdIds(), households, persons);
	}

	private List<HouseholdOfPanelDataId> householdIds() {
		return asList(householdId);
	}

	private SortedMap<Integer, Double> simpleDistribution() {
		SortedMap<Integer, Double> distribution = new TreeMap<>();
		distribution.put(household.domCode(), 1.0d);
		return distribution;
	}

	@Test
	public void worksForCorrectPerson() {
		calculateWeightsFor(correctPersons());

		Map<String, SortedMap<Integer, Double>> distributions = calculateWeightedDistributions();

		assertThat(distributions.keySet(), containsInAnyOrder("MALE", "FEMALE", "EMP"));
	}

	private Map<String, SortedMap<Integer, Double>> calculateWeightedDistributions() {
		return calculator.calculateWeightedDistributions(simpleDistribution(), simpleDistribution(),
				simpleDistribution(), simpleDistribution(), householdIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsForIncorrectSex() {
		calculateWeightsFor(incorrectPersons());
		calculateWeightedDistributions();
	}

	private Map<HouseholdOfPanelDataId, List<PersonOfPanelData>> incorrectPersons() {
		PersonOfPanelData incorrectSexPerson = ExamplePersonOfPanelData
				.personWithGender(incorrectGender);
		return singletonMap(householdId, asList(incorrectSexPerson));
	}
}
