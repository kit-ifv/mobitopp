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

  private static final RegionalContext someZoneContext = new DefaultRegionalContext(
      RegionalLevel.zone, "1");
  private static final RegionalContext otherZoneContext = new DefaultRegionalContext(
      RegionalLevel.zone, "2");

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
        .of(someZoneContext, List.of(householdAttribute));
    AttributeResolver resolver = new MultiLevelAttributeResolver(attributes, panelDataRepository);

    Map<String, Integer> resolvedAttributes = resolver.attributesOf(household, someZoneContext);

    assertThat(resolvedAttributes).containsEntry(name, value);
  }

  @Test
  void filterAttributesByType() throws Exception {
    Attribute otherAttribute = mock(Attribute.class);
    String name = "name";
    when(otherAttribute.type()).thenReturn(StandardAttribute.domCode);
    when(householdAttribute.name()).thenReturn(name);
    when(householdAttribute.type()).thenReturn(StandardAttribute.householdSize);
    Map<RegionalContext, List<Attribute>> attributes = Map
        .of(someZoneContext, List.of(householdAttribute, otherAttribute));
    AttributeResolver resolver = new MultiLevelAttributeResolver(attributes, panelDataRepository);

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
    Map<RegionalContext, List<Attribute>> attributes = Map
        .of(someZoneContext, List.of(householdAttribute), otherZoneContext,
            List.of(otherAttribute));
    AttributeResolver resolver = new MultiLevelAttributeResolver(attributes, panelDataRepository);

    List<Attribute> resolvedAttributes = resolver.attributesOf(StandardAttribute.householdSize);

    assertThat(resolvedAttributes).contains(householdAttribute).doesNotContain(otherAttribute);
  }
}
