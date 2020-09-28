package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder.personOfPanelData;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.data.local.ExamplePersonOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

@ExtendWith(MockitoExtension.class)
public class FemaleAgeTest {

	@Mock
  private RegionalContext context;

	@BeforeEach
	public void initialise() {
		lenient().when(context.name()).thenReturn("my-context-1");
	}

	@Test
  public void createsConstraint() {
    Demography demography = ExampleDemandZones.create().someZone().nominalDemography();
    FemaleAge femaleAge = newAttribute();

    Constraint constraint = femaleAge.createConstraint(demography);

    assertThat(constraint).isEqualTo(new SimpleConstraint(femaleAge.name(), 2));
    verify(context, times(2)).name();
  }

  @Test
  public void valueForHousehold() {
    FemaleAge femaleAge = newAttribute();

    HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household;
    PanelDataRepository panelDataRepository = mock(PanelDataRepository.class);
    PersonOfPanelData matchingConstraint = personOfPanelData()
        .withId(ExamplePersonOfPanelData.anId)
        .withGender(PersonOfPanelDataBuilder.genderFemale)
        .withAge(2)
        .build();
    PersonOfPanelData notMatchingConstraint = personOfPanelData()
        .withId(ExamplePersonOfPanelData.otherId)
        .withGender(PersonOfPanelDataBuilder.genderFemale)
        .withAge(6)
        .build();
    when(panelDataRepository.getPersonsOfHousehold(household.id()))
        .thenReturn(asList(matchingConstraint, notMatchingConstraint));

    int matchingPeople = femaleAge.valueFor(household, panelDataRepository);

    assertThat(matchingPeople, is(1));
  }

  private FemaleAge newAttribute() {
    return new FemaleAge(context, StandardAttribute.femaleAge, 0, 5);
  }
}
