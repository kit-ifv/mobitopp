package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;

@ExtendWith(MockitoExtension.class)
public class MultiLevelAttributeResolverTest {

  private static final RegionalContext someZoneContext = new DefaultRegionalContext(
      RegionalLevel.zone, "1");
  private static final RegionalContext otherZoneContext = new DefaultRegionalContext(
      RegionalLevel.zone, "2");

  @Mock
  private Attribute householdAttribute;

  @Test
  void filterAttributesByType() throws Exception {
    Attribute otherAttribute = mock(Attribute.class);
    String name = "name";
    when(otherAttribute.type()).thenReturn(StandardAttribute.domCode);
    when(householdAttribute.name()).thenReturn(name);
    when(householdAttribute.type()).thenReturn(StandardAttribute.householdSize);
    Map<RegionalContext, List<Attribute>> attributes = Map
        .of(someZoneContext, List.of(householdAttribute, otherAttribute));
    AttributeResolver resolver = new MultiLevelAttributeResolver(attributes);

    List<Attribute> resolvedAttributes = resolver.attributesOf(StandardAttribute.householdSize);

    assertThat(resolvedAttributes).contains(householdAttribute).doesNotContain(otherAttribute);
  }

  @Test
  void ensureAttributesAreDistinct() throws Exception {
    Attribute otherAttribute = mock(Attribute.class);
    String name = "name";
    when(otherAttribute.name()).thenReturn(name);
    when(otherAttribute.type()).thenReturn(StandardAttribute.householdSize);
    when(householdAttribute.type()).thenReturn(StandardAttribute.householdSize);
    when(householdAttribute.name()).thenReturn(name);
    Map<RegionalContext, List<Attribute>> attributes = new LinkedHashMap<>();
    attributes.put(someZoneContext, List.of(householdAttribute));
    attributes.put(otherZoneContext, List.of(otherAttribute));
    AttributeResolver resolver = new MultiLevelAttributeResolver(attributes);

    List<Attribute> resolvedAttributes = resolver.attributesOf(StandardAttribute.householdSize);

    assertThat(resolvedAttributes)
        .hasSize(1)
        .contains(householdAttribute)
        .doesNotContain(otherAttribute);
  }
}
