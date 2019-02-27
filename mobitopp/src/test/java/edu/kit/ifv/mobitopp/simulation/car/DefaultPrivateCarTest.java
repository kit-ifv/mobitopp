package edu.kit.ifv.mobitopp.simulation.car;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHousehold;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household;

public class DefaultPrivateCarTest {

  private static final int someOid = 0;
  private static final int somePersonNumber = 0;

  @Test
  void returnsCarToHousehold() throws Exception {
    Car car = mock(Car.class);
    Household owner = mock(Household.class);
    Zone zone = ExampleZones.create().someZone();
    HouseholdId householdId = ExampleHousehold.id;
    PersonId mainUser = new PersonId(someOid, householdId, somePersonNumber);
    PersonId personalUser = mainUser;
    
    DefaultPrivateCar privateCar = new DefaultPrivateCar(car, owner, mainUser, personalUser);
    
    privateCar.returnCar(zone);
    
    verify(car).returnCar(zone);
    verify(owner).returnCar(privateCar);
  }
}
