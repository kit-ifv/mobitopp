package edu.kit.ifv.mobitopp.data;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;

public class PopulationForSetupTest {

  @Test
  public void streamAllAddedHouseholds() {
    HouseholdForSetup familyMueller = mock(HouseholdForSetup.class);
    HouseholdForSetup familyMeier = mock(HouseholdForSetup.class);
    PopulationForSetup setup = new PopulationForSetup();

    setup.addHousehold(familyMueller);
    setup.addHousehold(familyMeier);

    List<HouseholdForSetup> households = setup.households().collect(toList());

    assertThat(households, hasItems(familyMueller, familyMeier));
  }
}
