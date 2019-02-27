package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.HouseholdForDemand;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class DefaultPrivateCarForSetupTest {

  private static final int someOid = 0;
  private static final int somePersonNumber = 1;
  private static final short year = 2000;
  private static final int otherOid = 2;

  @Test
  void convertToCar() throws Exception {
    Car car = mock(Car.class);
    HouseholdForDemand household = mock(HouseholdForDemand.class);
    HouseholdId householdId = ExampleHousehold.id;
    PersonId mainUser = new PersonId(someOid, householdId, somePersonNumber);
    PersonId personalUser = mainUser;
    
    when(household.getId()).thenReturn(householdId);
    
    DefaultPrivateCarForSetup carForSetup = new DefaultPrivateCarForSetup(car, householdId, mainUser, personalUser);

    PrivateCar privateCar = carForSetup.toCar(household);
    
    assertThat(privateCar.car(), is(sameInstance(car)));
    assertThat(privateCar.owner(), is(equalTo(household)));
    assertThat(privateCar.mainUser(), is(equalTo(mainUser)));
    assertThat(privateCar.personalUser(), is(equalTo(personalUser)));
  }
  
  @Test
  void verifiesOwner() throws Exception {
    Car car = mock(Car.class);
    HouseholdForDemand household = mock(HouseholdForDemand.class);
    HouseholdId householdId = new HouseholdId(someOid, year, 3);
    HouseholdId otherHousehold = new HouseholdId(otherOid, year, 3);
    PersonId mainUser = new PersonId(someOid, householdId, somePersonNumber);
    PersonId personalUser = mainUser;
    
    when(household.getId()).thenReturn(householdId);
    
    DefaultPrivateCarForSetup carForSetup = new DefaultPrivateCarForSetup(car, otherHousehold, mainUser, personalUser);
    
    assertThrows(IllegalArgumentException.class, () -> carForSetup.toCar(household));
  }
}
