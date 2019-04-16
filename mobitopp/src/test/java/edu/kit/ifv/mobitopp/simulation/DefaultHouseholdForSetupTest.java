package edu.kit.ifv.mobitopp.simulation;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class DefaultHouseholdForSetupTest {

  private Zone zone;

  @BeforeEach
  public void initialise() {
    zone = ExampleZones.create().someZone();
  }
  @Test
  public void addPeople() {
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
    HouseholdForSetup setupHousehold = new ExampleHousehold(zone).withCars(1).build();
    PrivateCarForSetup car = mock(PrivateCarForSetup.class);
    PrivateCar privateCar = mock(PrivateCar.class);
    when(car.toCar(any())).thenReturn(privateCar);

    setupHousehold.ownCars(asList(car));
    Household household = setupHousehold.toHousehold();

    assertThat(household.whichCars(), contains(privateCar));
  }

  @Test
  void getNumberOfPersonsInAgeRange() throws Exception {
    HouseholdForSetup household = new ExampleHousehold(zone).build();
    int lowerBound = 0;
    int youngAge = 1;
    int oldAge = 2;
    int upperBound = 3;
    household.addPerson(newPersonWithAge(youngAge));
    household.addPerson(newPersonWithAge(oldAge));
    
    assertAll(() -> assertThat(household.getNumberOfPersonsInAgeRange(lowerBound, upperBound), is(2)),
        () -> assertThat(household.getNumberOfPersonsInAgeRange(lowerBound, lowerBound), is(0)),
        () -> assertThat(household.getNumberOfPersonsInAgeRange(upperBound, upperBound), is(0)),
        () -> assertThat(household.getNumberOfPersonsInAgeRange(lowerBound, youngAge), is(1)),
        () -> assertThat(household.getNumberOfPersonsInAgeRange(youngAge, upperBound), is(2)),
        () -> assertThat(household.getNumberOfPersonsInAgeRange(oldAge, upperBound), is(1)),
        () -> assertThat(household.getNumberOfPersonsInAgeRange(lowerBound, oldAge), is(2)));
  }
  
  private PersonForSetup newPersonWithAge(int age) {
    PersonForSetup person = mock(PersonForSetup.class);
    when(person.age()).thenReturn(age);
    return person;
  }
}
