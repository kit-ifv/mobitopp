package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

@ExtendWith(MockitoExtension.class)
public class NamedAttributeTest {

	private static final int requestedWeight = 1;
  @Mock
	private RegionalContext context;
	private int lowerBound;
	private int upperBound;
	private AttributeType type;

	@BeforeEach
	public void initialise() {
		type = StandardAttribute.householdSize;
		lowerBound = 1;
		upperBound = lowerBound;
	}
	
	@Test
	void matchesDistributionItem() throws Exception {
		Attribute attribute = newAttribute();
		
		RangeDistributionItem matchingItem = new RangeDistributionItem(lowerBound, upperBound, 1);
		RangeDistributionItem notMatchingItem = new RangeDistributionItem(lowerBound - 1, upperBound - 1, 1);
		assertAll(
			() -> assertThat(attribute.matches(matchingItem)).isTrue(),
			() -> assertThat(attribute.matches(notMatchingItem)).isFalse()
		);
	}

	@Test
	void buildsUpName() throws Exception {
		String contextName = "my-context-1";
		when(context.name()).thenReturn(contextName);
		Attribute attribute = newAttribute();

		String name = attribute.name();

		String expectedName = contextName + DynamicHouseholdAttribute.nameSeparator
				+ type.createInstanceName(lowerBound, upperBound);
		assertThat(name).isEqualTo(expectedName);
		verify(context).name();
	}

	private Attribute newAttribute() {
		return new NamedAttribute(context, type, lowerBound, upperBound, requestedWeight) {

			@Override
			public int valueFor(HouseholdOfPanelData household, PanelDataRepository panelDataRepository) {
				throw new RuntimeException("Not necessary for test!");
			}

		};
	}
}
