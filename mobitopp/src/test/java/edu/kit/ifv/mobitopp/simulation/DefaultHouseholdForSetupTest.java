package edu.kit.ifv.mobitopp.simulation;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;

public class DefaultHouseholdForSetupTest {

  @Test
  public void addPeople() {
    Zone zone = ExampleZones.create().someZone();
    HouseholdForSetup setupHousehold = new ExampleHousehold(zone).withSize(1).build();
    PersonForSetup setupPerson = mock(PersonForSetup.class);
    int personOid = 1;
    int personNumber = 1;
    when(setupPerson.getId())
        .thenReturn(new PersonId(personOid, setupHousehold.getId(), personNumber));
    Person person = mock(Person.class);
    when(setupPerson.toPerson(any())).thenReturn(person);

    setupHousehold.addPerson(setupPerson);
    Household household = setupHousehold.toHousehold();

    assertThat(household.getPersons(), contains(person));
  }

  @Test
  public void addCars() {
    Zone zone = ExampleZones.create().someZone();
    HouseholdForSetup setupHousehold = new ExampleHousehold(zone).withCars(1).build();
    PrivateCarForSetup car = mock(PrivateCarForSetup.class);

    setupHousehold.ownCars(asList(car));
    Household household = setupHousehold.toHousehold();

    assertThat(household.whichCars(), contains(car));
  }
}
