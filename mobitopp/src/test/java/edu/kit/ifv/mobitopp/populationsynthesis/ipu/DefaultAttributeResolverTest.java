package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

@ExtendWith(MockitoExtension.class)
public class DefaultAttributeResolverTest {

	@Mock
	private Attribute householdAttribute;
	private PanelDataRepository panelDataRepository;

	@BeforeEach
	public void initialise() {
		panelDataRepository = mock(PanelDataRepository.class);
	}

	@Test
	public void resolveAttributes() {
		HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household;
		Integer value = 1;
		String name = "name";
		when(householdAttribute.valueFor(household, panelDataRepository)).thenReturn(value);
		when(householdAttribute.name()).thenReturn(name);
		List<Attribute> attributes = asList(householdAttribute);
		DefaultAttributeResolver resolver = new DefaultAttributeResolver(attributes,
				panelDataRepository);

		Map<String, Integer> resolvedAttributes = resolver.attributesOf(household);

		assertThat(resolvedAttributes).containsEntry(name, value);
	}

	@Test
	void filterAttributesByType() throws Exception {
		Attribute otherAttribute = mock(Attribute.class);
		when(otherAttribute.attributeType()).thenReturn(StandardAttribute.domCode);
		when(householdAttribute.attributeType()).thenReturn(StandardAttribute.householdSize);
		List<Attribute> attributes = List.of(householdAttribute, otherAttribute);
		DefaultAttributeResolver resolver = new DefaultAttributeResolver(attributes,
				panelDataRepository);

		List<Attribute> resolvedAttributes = resolver.attributesOf(StandardAttribute.householdSize);

		assertThat(resolvedAttributes).contains(householdAttribute).doesNotContain(otherAttribute);
	}

}
