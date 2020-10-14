package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder.householdOfPanelData;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

@ExtendWith(MockitoExtension.class)
public class DynamicHouseholdAttributeTest {

  private static final int available = 1;
  private static final int notAvailable = 0;
  
  @Mock
  private RegionalContext context;
  private int lowerBound;
  private int upperBound;
  private int amount;
  private AttributeType type;
  private PanelDataRepository panelDataRepository;

  @BeforeEach
  public void initialise() {
  	lenient().when(context.name()).thenReturn("my-context-1");
    type = StandardAttribute.householdSize;
    lowerBound = 1;
    upperBound = lowerBound;
    amount = 2;
    panelDataRepository = mock(PanelDataRepository.class);
  }

  @Test
  public void valueForNotMatchingHousehold() {
    int tooLarge = upperBound + 1;
    HouseholdOfPanelData household = householdOfPanelData().withSize(tooLarge).build();
    DynamicHouseholdAttribute attribute = newAttribute(amount, HouseholdOfPanelData::size);

    int value = attribute.valueFor(household, panelDataRepository);

    assertThat(value, is(notAvailable));

    verifyZeroInteractions(panelDataRepository);
  }

  @Test
  public void valueForMatchingHousehold() {
    HouseholdOfPanelData household = householdOfPanelData().withSize(lowerBound).build();
    DynamicHouseholdAttribute attribute = newAttribute(amount, HouseholdOfPanelData::size);

    int value = attribute.valueFor(household, panelDataRepository);

    assertThat(value, is(available));

    verifyZeroInteractions(panelDataRepository);
  }

  private DynamicHouseholdAttribute newAttribute(
      int requestedWeight, Function<HouseholdOfPanelData, Integer> householdValue) {
    return new DynamicHouseholdAttribute(context, type, lowerBound, upperBound, requestedWeight, householdValue);
  }
}
