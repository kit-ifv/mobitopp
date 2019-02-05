package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.ExamplePersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class DefaultAttributeResolverTest {

	private static final String maleAgePrefix = StandardAttribute.maleAge.prefix();
  private static final String householdSizePrefix = StandardAttribute.householdSize.prefix();
  private PanelDataRepository panelDataRepository;

	@Before
	public void initialise() {
		panelDataRepository = mock(PanelDataRepository.class);
	}

	@Test
	public void resolvesHouseholdAttributes() {
		HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household;
		int lowerSize = household.size() - 1;
		List<Attribute> attributes = new ArrayList<>();
		attributes.add(new HouseholdSize(householdSizePrefix, household.size()));
		attributes.add(new HouseholdSize(householdSizePrefix, lowerSize));
		DefaultAttributeResolver resolver = new DefaultAttributeResolver(attributes,
				panelDataRepository);

		Map<String, Integer> resolvedAttributes = resolver.attributesOf(household);

		assertThat(resolvedAttributes, hasEntry(householdSizePrefix + household.size(), 1));
		assertThat(resolvedAttributes, hasEntry(householdSizePrefix + lowerSize, 0));
	}

	@Test
	public void resolvesPersonAttributes() {
		HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household;
		PersonOfPanelData aPerson = ExamplePersonOfPanelData.person(ExamplePersonOfPanelData.anId);
		when(panelDataRepository.getPersonsOfHousehold(household.id())).thenReturn(asList(aPerson));
		List<Attribute> attributes = new ArrayList<>();
		int olderAge = aPerson.age() + 1;
		attributes.add(new MaleAge(maleAgePrefix, aPerson.age(), aPerson.age()));
		attributes.add(new MaleAge(maleAgePrefix, olderAge, olderAge));
		DefaultAttributeResolver resolver = new DefaultAttributeResolver(attributes,
				panelDataRepository);

		Map<String, Integer> resolvedAttributes = resolver.attributesOf(household);

		assertThat(resolvedAttributes, hasEntry(maleAgePrefix + aPerson.age() + "-" + aPerson.age(), 1));
		assertThat(resolvedAttributes, hasEntry(maleAgePrefix + olderAge + "-" + olderAge, 0));
	}
}
