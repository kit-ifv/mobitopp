package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

@ExtendWith(MockitoExtension.class)
public class MultiLevelAttributeResolverTest {

	private static final RegionalContext zoneContext = new DefaultRegionalContext(RegionalLevel.zone,
			"1");

	@Mock
	private Attribute householdAttribute;
	@Mock
	private PanelDataRepository panelDataRepository;

	@Test
	public void resolveAttributes() {
		HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household;
		Integer value = 1;
		String name = "name";
		when(householdAttribute.valueFor(household, panelDataRepository)).thenReturn(value);
		when(householdAttribute.name()).thenReturn(name);
		Map<RegionalContext, List<Attribute>> attributes = Map
				.of(zoneContext, List.of(householdAttribute));
		AttributeResolver resolver = new MultiLevelAttributeResolver(attributes, panelDataRepository);

		Map<String, Integer> resolvedAttributes = resolver.attributesOf(household, zoneContext);

		assertThat(resolvedAttributes).containsEntry(name, value);
	}

	@Test
	void filterAttributesByType() throws Exception {
		Attribute otherAttribute = mock(Attribute.class);
		when(otherAttribute.type()).thenReturn(StandardAttribute.domCode);
		when(householdAttribute.type()).thenReturn(StandardAttribute.householdSize);
		Map<RegionalContext, List<Attribute>> attributes = Map
				.of(zoneContext, List.of(householdAttribute, otherAttribute));
		AttributeResolver resolver = new MultiLevelAttributeResolver(attributes, panelDataRepository);

		List<Attribute> resolvedAttributes = resolver.attributesOf(StandardAttribute.householdSize);

		assertThat(resolvedAttributes).contains(householdAttribute).doesNotContain(otherAttribute);
	}
}
