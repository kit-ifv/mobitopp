package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DefaultAttributeResolverTest {

  private PanelDataRepository panelDataRepository;

  @Before
  public void initialise() {
    panelDataRepository = mock(PanelDataRepository.class);
  }

  @Test
  public void resolveAttributes() {
    HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household;
    Integer value = 1;
    String name = "name";
    Attribute attribute = mock(Attribute.class);
    when(attribute.valueFor(household, panelDataRepository)).thenReturn(value);
    when(attribute.name()).thenReturn(name);
    List<Attribute> attributes = asList(attribute);
    DefaultAttributeResolver resolver = new DefaultAttributeResolver(attributes,
        panelDataRepository);

    Map<String, Integer> resolvedAttributes = resolver.attributesOf(household);

    assertThat(resolvedAttributes, hasEntry(name, value));
  }

}
