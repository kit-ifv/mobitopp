package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultAttributeResolverTest {

	@Mock
	private Attribute householdAttribute;

	@Test
	void filterAttributesByType() throws Exception {
		Attribute otherAttribute = mock(Attribute.class);
		when(otherAttribute.type()).thenReturn(StandardAttribute.domCode);
		when(householdAttribute.type()).thenReturn(StandardAttribute.householdSize);
		List<Attribute> attributes = List.of(householdAttribute, otherAttribute);
		DefaultAttributeResolver resolver = new DefaultAttributeResolver(attributes);

		List<Attribute> resolvedAttributes = resolver.attributesOf(StandardAttribute.householdSize);

		assertThat(resolvedAttributes).contains(householdAttribute).doesNotContain(otherAttribute);
	}
	
}
