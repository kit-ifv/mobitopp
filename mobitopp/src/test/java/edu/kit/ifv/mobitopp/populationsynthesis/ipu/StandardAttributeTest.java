package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.demand.Demography;

public class StandardAttributeTest {

	@Test
	void nameOfAttributesContainsContext() throws Exception {
		Demography demography = ExampleDemandZones.create().someZone().nominalDemography();
		String contextName = "my-context-1";
		RegionalContext context = mock(RegionalContext.class);
		when(context.name()).thenReturn(contextName);
		Stream<Attribute> attributes = Stream
				.of(StandardAttribute.householdSize, StandardAttribute.femaleAge)
				.flatMap(type -> type.createAttributes(demography, context));

		assertThat(attributes).allMatch(attribute -> attribute.name().startsWith(contextName));
	}
}
