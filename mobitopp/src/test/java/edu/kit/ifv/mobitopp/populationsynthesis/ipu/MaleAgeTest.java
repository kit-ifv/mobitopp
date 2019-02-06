package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder.personOfPanelData;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.ExamplePersonOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class MaleAgeTest {

  @Test
  public void createsConstraint() {
    Demography demography = ExampleDemandZones.create().someZone().nominalDemography();
    MaleAge maleAge = new MaleAge(StandardAttribute.maleAge, 0, 10);

    Constraint constraint = maleAge.createConstraint(demography);

    assertThat(constraint, is(equalTo(new PersonConstraint(4, maleAge.name()))));
  }

  @Test
  public void valueForHousehold() {
    MaleAge maleAge = new MaleAge(StandardAttribute.maleAge, 0, 5);

    HouseholdOfPanelData household = ExampleHouseholdOfPanelData.household;
    PanelDataRepository panelDataRepository = mock(PanelDataRepository.class);
    PersonOfPanelData matchingConstraint = personOfPanelData()
        .withId(ExamplePersonOfPanelData.anId)
        .withGender(PersonOfPanelDataBuilder.genderMale)
        .withAge(2)
        .build();
    PersonOfPanelData notMatchingConstraint = personOfPanelData()
        .withId(ExamplePersonOfPanelData.otherId)
        .withGender(PersonOfPanelDataBuilder.genderMale)
        .withAge(6)
        .build();
    when(panelDataRepository.getPersonsOfHousehold(household.id()))
        .thenReturn(asList(matchingConstraint, notMatchingConstraint));

    int matchingPeople = maleAge.valueFor(household, panelDataRepository);

    assertThat(matchingPeople, is(1));
  }
}
